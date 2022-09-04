package com.leih.productservice.dao;

import com.leih.shopping.model.Product;

import java.util.List;

public interface ProductDao {

    public Product queryProductById(long productId);
    public List<Product> getAllProducts();

}
