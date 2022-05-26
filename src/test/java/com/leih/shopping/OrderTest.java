package com.leih.shopping;

import com.leih.shopping.db.mappers.OrderMapper;
import com.leih.shopping.db.po.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@SpringBootTest
public class OrderTest {
    @Autowired
    OrderMapper orderMapper;
    @Test
    public void addOrder(){
        Order order= new Order();
        order.setOrderNo("123456");
        order.setOrderAmount(BigDecimal.valueOf(22L));
        order.setOrderStatus(1);
        order.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));
        order.setDealId(19L);
        order.setPaymentTime(null);
        order.setUserId(1L);
        orderMapper.insert(order);
    }
    @Test
    public void getOrder(){
        Order order = orderMapper.selectOrderByNo("123456");
        System.out.println(order);
    }
}
