package com.leih.productservice.service;

import com.leih.productservice.dao.ProductDao;
import com.leih.shopping.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    @Autowired
    ProductDao productDao;
    public Product getProductById(long productId){
        return productDao.queryProductById(productId);
    }
    public List<Product> getAllProducts(){
        return productDao.getAllProducts();
    }

}
