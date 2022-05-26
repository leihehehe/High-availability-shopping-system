package com.leih.shopping.service;

import com.alibaba.fastjson.JSON;
import com.leih.shopping.db.dao.DealDao;
import com.leih.shopping.db.dao.OrderDao;
import com.leih.shopping.db.po.Deal;
import com.leih.shopping.db.po.Order;
import com.leih.shopping.util.SnowFlake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class OrderService {
    @Autowired
    private RedisService redisService;
    @Autowired
    DealDao dealDao;
    @Autowired
    KafkaConsumerService kafkaConsumerService;
    @Autowired
    KafkaProducerService kafkaProducerService;
    @Autowired
    OrderDao orderDao;
    //this can be got from the system configuration.
    private final SnowFlake snowFlake = new SnowFlake(1,1);
    public boolean stockValidator(long dealId){
        String key ="stock:"+dealId;

        return redisService.deductItemFromRedisValidator(key);
    }
    public Order createOrder(long dealId,long userId){
        Deal deal = dealDao.queryDealById(dealId);
        Order order = new Order();
        order.setOrderNo(String.valueOf(snowFlake.nextId()));
        order.setDealId(dealId);
        order.setUserId(userId);
        order.setOrderAmount(deal.getDealPrice());
        //send order message to mq
        kafkaProducerService.sendMessage("order", JSON.toJSONString(order));
        //send check order message
        kafkaProducerService.sendMessage("checkOrder_15_minutes", order.getOrderNo());
        return order;
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
        boolean deductItem = dealDao.deductItem(order.getDealId());
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
