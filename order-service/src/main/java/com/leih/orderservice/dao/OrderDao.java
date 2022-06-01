package com.leih.orderservice.dao;

import com.leih.commonutil.model.Order;

public interface OrderDao {
    Order queryOrder(String orderNo);
    void insertOrder(Order order);
    void updateOrder(Order order);
}
