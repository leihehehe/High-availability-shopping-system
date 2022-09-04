package com.leih.dealservice.service;

import com.leih.shopping.common.result.ResultData;
import com.leih.shopping.model.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("productservice")
public interface ProductApi {
    @GetMapping("/api/product/inner/product/{productId}")
    ResultData<Product> getProduct(@PathVariable("productId") long productId);

}
