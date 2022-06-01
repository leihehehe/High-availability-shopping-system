package com.leih.productservice.controller;

import com.leih.commonutil.model.Product;
import com.leih.commonutil.util.ResultData;
import com.leih.productservice.service.ProductService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@MapperScan(basePackages = {"com.leih.productservice.mapper"})
public class ProductController {
    @Autowired
    ProductService productService;
    @GetMapping("/product/{productId}")
    public ResultData<Product> getProduct(@PathVariable("productId") long productId){
        return ResultData.success(productService.getProductById(productId));
    }
}
