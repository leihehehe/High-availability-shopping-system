package com.leih.orderservice.service;

import com.alibaba.fastjson.JSON;
import com.leih.orderservice.dao.OrderDao;
import com.leih.shopping.common.constant.RedisConstants;
import com.leih.shopping.common.util.SnowFlake;
import com.leih.shopping.common.constant.RabbitMQConstants;
import com.leih.shopping.common.result.ResultCodeEnum;
import com.leih.shopping.common.result.ResultData;
import com.leih.shopping.model.Deal;
import com.leih.shopping.model.Order;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class OrderService {
    @Autowired
    private RedisService redisService;
//    @Autowired
//    KafkaTemplate<String,String> kafkaTemplate;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    OrderDao orderDao;
    @Autowired
    DealApi dealApi;
    @Autowired
    RedissonClient redissonClient;
    Logger logger  = LoggerFactory.getLogger(OrderService.class);
    //this can be got from the system configuration.
    private final SnowFlake snowFlake = new SnowFlake(1,1);
    public boolean stockValidator(long dealId){
        //check if deal id exists in the bloom filter
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(RedisConstants.STOCK_BLOOM_FILTER);
        if(!bloomFilter.contains(dealId)){
            logger.info("The deal " +dealId+" does not exist");
            return false;
        }
        logger.info("The deal " +dealId+" exists");
        String key = RedisConstants.STOCK_PREFIX +dealId;
        return redisService.deductItemFromRedisValidator(key,dealId);
    }

    @CircuitBreaker(name="orderService",fallbackMethod = "orderCallBack")
    public ResultData<Order> createOrder(long dealId, long userId){
        //get target deal and create order
        Deal deal = dealApi.getDealById(dealId).getData();
        if(deal !=null){
            Order order = new Order();
            order.setOrderNo(String.valueOf(snowFlake.nextId()));
            order.setDealId(dealId);
            order.setUserId(userId);
            order.setOrderAmount(deal.getDealPrice());
            //send order message to mq
            rabbitTemplate.convertAndSend(RabbitMQConstants.SHOPPING_EXCHANGE,"createOrder.message",JSON.toJSONString(order));
            rabbitTemplate.convertAndSend(RabbitMQConstants.DELAYED_EXCHANGE,RabbitMQConstants.DELAYED_ROUTE_KEY,order.getOrderNo(),message -> {
                message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);  //persistent msg
                message.getMessageProperties().setDelay(300 * 1000);   // 5 minutes
                return message;
            });
//            kafkaTemplate.send("order", JSON.toJSONString(order));
            //send check order message
//            kafkaTemplate.send("checkOrder_15_minutes", order.getOrderNo());
            return ResultData.success(order);
        }
        return ResultData.fail(ResultCodeEnum.DEAL_NO_EXIST);
    }
    public ResultData<Order> orderCallBack(Exception exception){
        logger.info("deal service down");
        return ResultData.fail(ResultCodeEnum.SERVICE_ERROR);
    }

    public Order getOrderByNo(String orderNo){
       return orderDao.queryOrder(orderNo);
    }
    public Order[] getOrdersByUserId(String userId){
        return orderDao.getOrdersByUserId(userId);
    }

    public Order processPayment(String orderNo){
        Order order = orderDao.queryOrder(orderNo);
        if(order ==null ||order.getOrderStatus()!=1){
            //already paid
            return order;
        }
        boolean deductItem = dealApi.deductItem(order.getDealId()).getData();
        if(deductItem){
            //if deducted the item successfully
            order.setOrderStatus(2);//paid
            order.setPaymentTime(new Date());
            orderDao.updateOrder(order);
        }else{
            order.setOrderStatus(0);//out of stock
            orderDao.updateOrder(order);
            //revert stock back, update deal
            dealApi.revertItem(order.getDealId());
            //remove the user from the restricted list
            redisService.removeUserLimits(order.getDealId(), order.getUserId());
            logger.info("The user has been removed from the restricted list since the item is out of stock");
        }
        return order;
    }
}
