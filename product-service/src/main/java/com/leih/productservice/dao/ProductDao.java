package com.leih.productservice.dao;

import com.leih.commonutil.model.Product;
public interface ProductDao {

    public Product queryProductById(long productId);
}
