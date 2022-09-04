package com.leih.orderservice.dao;

import com.leih.shopping.model.Order;

public interface OrderDao {
    Order queryOrder(String orderNo);
    void insertOrder(Order order);
    void updateOrder(Order order);
    Order[] getOrdersByUserId(String userId);
}
