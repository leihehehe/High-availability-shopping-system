package com.leih.dealservice.controller;

import com.leih.dealservice.service.DealService;
//import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import com.leih.shopping.common.result.ResultCodeEnum;
import com.leih.shopping.common.result.ResultData;
import com.leih.shopping.model.Deal;
import com.leih.shopping.model.Product;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
public class DealController {
    @Autowired
    DealService dealService;
    Logger logger = LoggerFactory.getLogger(DealController.class);
/*    @GetMapping("/addDeal")
    public String addDeal(){
        return "add_deal";
    }*/
    @GetMapping("/deal")
    @RateLimiter(name = "dealService", fallbackMethod = "getFallBack")
    public ResultData<List<Deal>> dealList(){
        return ResultData.success(dealService.getDealByStatus(1));
    }
    /***
     * This method is to limit the rate
     * @param exception
     * @return
     */
    @GetMapping
    private ResultData<List<Deal>> getFallBack(Exception exception) {
        logger.info("exceed rate limit");
//        return ResultData.fail(ReturnCode.RC300.getCode(),"The system is busy");
        return ResultData.fail(ResultCodeEnum.SERVICE_ERROR);
    }

    @GetMapping("/deal/{dealId}")
    public ResultData<Deal> getDeal(@PathVariable("dealId") long dealId){

        return ResultData.success(dealService.getDealById(dealId));
    }


    @GetMapping("/deal/product/{dealId}")
    public ResultData<Product> getDealProduct(
            @PathVariable("dealId") long dealId
    ){
        return dealService.getProduct(dealId);
    }

    /**
     * This method could be used to get system time
     * @return
     */
    @ResponseBody
    @GetMapping("/deal/getSystemTime/")
    public ResultData<Date> getSystemTime(){

        return ResultData.success(new Date());
    }

}
