package com.leih.productservice.service;

import com.leih.productservice.dao.ProductDao;
import com.leih.productservice.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    @Autowired
    ProductDao productDao;
    public Product getProductById(long productId){
        return productDao.queryProductById(productId);
    }
}
