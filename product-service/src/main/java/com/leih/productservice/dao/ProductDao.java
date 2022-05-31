package com.leih.productservice.dao;

import com.leih.productservice.model.Product;
public interface ProductDao {

    public Product queryProductById(long productId);
}
