package com.leih.shopping.service;

import com.leih.shopping.db.dao.ProductDao;
import com.leih.shopping.db.po.Product;
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
