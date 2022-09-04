package com.leih.orderservice.service;

import com.leih.shopping.common.result.ResultData;
import com.leih.shopping.model.Deal;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

@FeignClient("dealservice")
public interface DealApi {
    @GetMapping("/api/deal/inner/deal/{dealId}")
    public ResultData<Deal> getDealById(@PathVariable("dealId") long dealId);
    @DeleteMapping("/api/deal/inner/deductItem/{dealId}")
    public ResultData<Boolean> deductItem(@PathVariable("dealId") long dealId);
    @PutMapping("/api/deal/inner/lockItem/{dealId}")
    public ResultData<Boolean> lockItem(@PathVariable("dealId") long dealId);

    @PutMapping("/api/deal/inner/revertItem/{dealId}")
    public ResultData<Boolean> revertItem(@PathVariable("dealId") long dealId);

}
