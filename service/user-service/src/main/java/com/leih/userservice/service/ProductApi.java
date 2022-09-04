package com.leih.userservice.service;

import com.leih.shopping.common.result.ResultData;
import com.leih.shopping.model.Deal;
import com.leih.shopping.model.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient("productservice")
public interface ProductApi {
    @GetMapping("/api/product/inner/products")
    public ResultData<List<Product>> getAllProducts();

}
