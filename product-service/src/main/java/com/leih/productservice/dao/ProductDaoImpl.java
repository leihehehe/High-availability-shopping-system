package com.leih.productservice.dao;

import com.leih.productservice.mapper.ProductMapper;
import com.leih.productservice.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


@Repository
public class ProductDaoImpl implements ProductDao {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public Product queryProductById(long productId) {
        return productMapper.selectByPrimaryKey(productId);
    }
}
