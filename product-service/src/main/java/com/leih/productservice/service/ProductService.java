package com.leih.productservice.service;

import com.leih.commonutil.model.Product;
import com.leih.productservice.dao.ProductDao;
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
