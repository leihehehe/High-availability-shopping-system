package com.leih.dealservice.controller;

import com.leih.dealservice.service.DealService;
//import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import com.leih.commonutil.model.Deal;
import com.leih.commonutil.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import com.leih.commonutil.util.ResultData;

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
//    @RateLimiter(name = "backendB", fallbackMethod = "getFallBack")
    public ResultData<List<Deal>> dealList(){

        return ResultData.success(dealService.getDealByStatus(1));
    }
    @GetMapping("/deal/{dealId}")
    public ResultData<Deal> getDeal(@PathVariable("dealId") long dealId){
        return ResultData.success(dealService.getDealById(dealId));
    }


 /*   *//***
     * This method is to limit the rate
     * @param throwable
     * @return
     *//*
    @GetMapping
    private String getFallBack(Throwable throwable) {
        logger.info("exceed limit");
        return null;
    }*/

    /***
     * This method is used to add deal
     * @param name
     * @param productId
     * @param originalPrice
     * @param dealPrice
     * @param dealQuantity
     * @param startTime
     * @param endTime
     * @return
     * @throws ParseException
     */
    @PostMapping("/deal/addDealAction")
    public ResultData<Deal> addDealAction(
            @RequestParam("name") String name,
            @RequestParam("productId") long productId,
            @RequestParam("originalPrice") BigDecimal originalPrice,
            @RequestParam("dealPrice") BigDecimal dealPrice,
            @RequestParam("dealQuantity") long dealQuantity,
            @RequestParam("startTime") String startTime,
            @RequestParam("endTime") String endTime

    ) throws ParseException {
        startTime = startTime.substring(0, 10) +  startTime.substring(11);
        endTime = endTime.substring(0, 10) +  endTime.substring(11);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-ddhh:mm");
        Deal deal = new Deal();
        deal.setName(name);
        deal.setProductId(productId);
        deal.setDealPrice(dealPrice);
        deal.setOriginalPrice(originalPrice);
        deal.setTotalStock(dealQuantity);
        deal.setAvailableStock((int) dealQuantity);
        deal.setLockStock(0L);
        deal.setDealStatus(1);
        deal.setStartTime(format.parse(startTime));
        deal.setEndTime(format.parse(endTime));
        //insert created deal into the database
        dealService.insertDeal(deal);
        return ResultData.success(deal);
    }

    @GetMapping("/deal/product/{dealId}")
    public ResultData<Product> getDealProduct(
            @PathVariable("dealId") long dealId
    ){
        return dealService.getProduct(dealId);
    }

    /**
     * This method could be used to get system time for checking the start times of deals
     * @param dealId deal id
     * @return
     */
    @ResponseBody
    @GetMapping("/deal/getSystemTime/{dealId}")
    public ResultData<String> getSystemTime(@PathVariable("dealId") Long dealId){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String date = simpleDateFormat.format(new Date());

        return ResultData.success(date);
    }

    @DeleteMapping("/deal/deductItem/{dealId}")
    public ResultData<Boolean> deductItem(@PathVariable("dealId") Long dealId){
        boolean result = dealService.deductItemForDeal(dealId);
        return ResultData.success(result);
    }

    @PutMapping("/deal/lockItem/{dealId}")
    public ResultData<Boolean> lockItem(@PathVariable("dealId") Long dealId){
        boolean result = dealService.lockItem(dealId);
        return ResultData.success(result);
    }
    @PutMapping("/deal/revertItem/{dealId}")
    public ResultData<Object> revertItem(@PathVariable("dealId") Long dealId){
        dealService.revertItem(dealId);
        return ResultData.success(null);
    }
}
