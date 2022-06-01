package com.leih.orderservice.service;

import com.alibaba.fastjson.JSON;
import com.leih.commonutil.api.DealApi;
import com.leih.commonutil.util.ResultData;
import com.leih.commonutil.util.ReturnCode;
import com.leih.orderservice.dao.OrderDao;
import com.leih.orderservice.util.SnowFlake;
import com.leih.commonutil.model.Deal;
import com.leih.commonutil.model.Order;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class OrderService {
    @Autowired
    private RedisService redisService;
    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;
    @Autowired
    OrderDao orderDao;
    @Autowired
    DealApi dealApi;
    Logger logger  = LoggerFactory.getLogger(OrderService.class);
    //this can be got from the system configuration.
    private final SnowFlake snowFlake = new SnowFlake(1,1);
    public boolean stockValidator(long dealId){
        String key ="stock:"+dealId;

        return redisService.deductItemFromRedisValidator(key);
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
            kafkaTemplate.send("order", JSON.toJSONString(order));
            //send check order message
            kafkaTemplate.send("checkOrder_15_minutes", order.getOrderNo());
            return ResultData.success(order);
        }
        return ResultData.fail(ReturnCode.DEAL_NO_EXIST.getCode(), "No such deal exists, please try again.");
    }
    public ResultData<Order> orderCallBack(Exception exception){
        logger.info("deal service down");
        return ResultData.fail(ReturnCode.RC500.getCode(), "Sorry, the server is down.");
    }

    public Order getOrderByNo(String orderNo){
       return orderDao.queryOrder(orderNo);
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
            //refund...
        }
        return order;
    }
}
