package com.leih.productservice.dao;

import com.leih.productservice.mapper.ProductMapper;
import com.leih.shopping.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class ProductDaoImpl implements ProductDao {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public Product queryProductById(long productId) {
        return productMapper.selectByPrimaryKey(productId);
    }

    @Override
    public List<Product> getAllProducts() {
        return productMapper.getAllProducts();
    }
}
