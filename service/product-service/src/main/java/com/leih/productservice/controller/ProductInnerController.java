package com.leih.productservice.controller;

import com.leih.productservice.service.ProductService;
import com.leih.shopping.common.result.ResultData;
import com.leih.shopping.model.Product;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@MapperScan(basePackages = {"com.leih.productservice.mapper"})
@RequestMapping("/api/product/inner")
public class ProductInnerController {
    @Autowired
    ProductService productService;
    @GetMapping("/product/{productId}")
    public ResultData<Product> getProduct(@PathVariable("productId") long productId){
        return ResultData.success(productService.getProductById(productId));
    }
    @GetMapping("/products")
    public ResultData<List<Product>> getAllProduct(){
        return ResultData.success(productService.getAllProducts());
    }
}
