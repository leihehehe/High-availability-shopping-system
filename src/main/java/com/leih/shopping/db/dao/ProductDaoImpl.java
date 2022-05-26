package com.leih.shopping.db.dao;

import com.leih.shopping.db.mappers.ProductMapper;
import com.leih.shopping.db.po.Product;

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
