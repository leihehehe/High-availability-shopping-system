package com.leih.shopping.db.dao;

import com.leih.shopping.db.po.Order;

public interface OrderDao {
    Order queryOrder(String orderNo);
    void insertOrder(Order order);
    void updateOrder(Order order);
}
