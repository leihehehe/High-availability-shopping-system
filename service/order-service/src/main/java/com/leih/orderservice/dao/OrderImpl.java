package com.leih.orderservice.dao;

import com.leih.orderservice.mapper.OrderMapper;
import com.leih.shopping.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class OrderImpl implements OrderDao {
    @Autowired
    OrderMapper orderMapper;
    @Override
    public Order queryOrder(String orderNo) {
        return orderMapper.selectOrderByNo(orderNo);
    }

    @Override
    public void insertOrder(Order order) {
        orderMapper.insert(order);
    }

    @Override
    public void updateOrder(Order order) {
        orderMapper.updateByPrimaryKey(order);
    }

    @Override
    public Order[] getOrdersByUserId(String userId) {
        return orderMapper.getOrdersByUserId(userId);
    }
}
