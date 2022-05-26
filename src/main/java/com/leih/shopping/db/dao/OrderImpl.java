package com.leih.shopping.db.dao;

import com.leih.shopping.db.mappers.OrderMapper;
import com.leih.shopping.db.po.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class OrderImpl implements OrderDao{
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
}
