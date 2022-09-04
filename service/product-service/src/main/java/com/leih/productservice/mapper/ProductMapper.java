package com.leih.productservice.mapper;

import com.leih.shopping.model.Product;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Long id);
    List<Product> getAllProducts();

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);
}