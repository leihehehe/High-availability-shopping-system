package com.leih.commonutil.api;

import com.leih.commonutil.model.Deal;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import com.leih.commonutil.util.ResultData;

import java.util.List;

@FeignClient("dealservice")
public interface DealApi {
    @GetMapping("/deal/{dealId}")
    public ResultData<Deal> getDealById(@PathVariable("dealId") long dealId);
    @DeleteMapping("/deal/deductItem/{dealId}")
    public ResultData<Boolean> deductItem(@PathVariable("dealId") long dealId);
    @PutMapping("/deal/lockItem/{dealId}")
    public ResultData<Boolean> lockItem(@PathVariable("dealId") long dealId);

    @PutMapping("/deal/revertItem/{dealId}")
    public ResultData<Boolean> revertItem(@PathVariable("dealId") long dealId);

    @GetMapping("/deal")
    public ResultData<List<Deal>> getDeals();
}
