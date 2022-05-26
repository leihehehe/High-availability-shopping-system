package com.leih.shopping.db.dao;

import com.leih.shopping.db.po.Product;
public interface ProductDao {

    public Product queryProductById(long productId);
}
