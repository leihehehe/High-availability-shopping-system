package com.leih.userservice.service;

import com.leih.shopping.common.result.ResultData;
import com.leih.shopping.model.Deal;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@FeignClient("dealservice")
public interface DealApi {
    @GetMapping("/api/deal/inner/deal/{dealId}")
    public ResultData<Deal> getDealById(@PathVariable("dealId") long dealId);
    @GetMapping("/api/deal/inner/deals")
    public ResultData<List<Deal>> getAllDeals();
    @PostMapping("/api/deal/inner/addDealAction")
    public ResultData<Deal> addDealAction(@RequestParam String name, @RequestParam long productId, @RequestParam BigDecimal originalPrice, @RequestParam BigDecimal dealPrice,@RequestParam long dealQuantity, @RequestParam int dealStatus);
    @PostMapping("/api/deal/inner/changeDealStatus")
    public ResultData<Deal> changeDealStatus(@RequestParam long dealId, @RequestParam int dealStatus);

}
