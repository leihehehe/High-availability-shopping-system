package com.leih.orderservice.service;

import com.alibaba.fastjson.JSON;
import com.leih.orderservice.dao.OrderDao;
import com.leih.orderservice.model.Deal;
import com.leih.orderservice.model.Order;
import com.leih.orderservice.util.SnowFlake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
    RestTemplate restTemplate;
    //this can be got from the system configuration.
    private final SnowFlake snowFlake = new SnowFlake(1,1);
    public boolean stockValidator(long dealId){
        String key ="stock:"+dealId;

        return redisService.deductItemFromRedisValidator(key);
    }

    public Order createOrder(long dealId,long userId){
        //get target deal and create order
        String dealUrl="http://localhost:8083/deal/"+dealId;
        Deal deal = restTemplate.getForObject(dealUrl, Deal.class);
        Order order = new Order();
        order.setOrderNo(String.valueOf(snowFlake.nextId()));
        order.setDealId(dealId);
        order.setUserId(userId);
        order.setOrderAmount(deal.getDealPrice());
        //send order message to mq
        kafkaTemplate.send("order", JSON.toJSONString(order));
        //send check order message
        kafkaTemplate.send("checkOrder_15_minutes", order.getOrderNo());
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
        String url="http://localhost:8083/deal/deductItem/"+ order.getDealId();
        Boolean deductItem = restTemplate.getForObject(url, Boolean.class);
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
