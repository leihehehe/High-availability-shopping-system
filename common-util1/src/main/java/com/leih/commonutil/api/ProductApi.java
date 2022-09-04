package com.leih.commonutil.api;

import com.leih.commonutil.model.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.leih.commonutil.util.ResultData;

@FeignClient("productservice")
public interface ProductApi {
    @GetMapping("/product/{productId}")
    ResultData<Product> getProduct(@PathVariable("productId") long productId);

}
