package com.leih.dealservice.controller;

import com.leih.dealservice.model.Deal;
import com.leih.dealservice.model.Product;
import com.leih.dealservice.service.DealService;
//import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    public List<Deal> dealList(){

        return dealService.getDealByStatus(1);
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
     * @param resultMap
     * @return
     * @throws ParseException
     */
    @PostMapping("/deal/addDealAction")
    public Deal addDealAction(
            @RequestParam("name") String name,
            @RequestParam("productId") long productId,
            @RequestParam("originalPrice") BigDecimal originalPrice,
            @RequestParam("dealPrice") BigDecimal dealPrice,
            @RequestParam("dealQuantity") long dealQuantity,
            @RequestParam("startTime") String startTime,
            @RequestParam("endTime") String endTime,
            Map<String,Object> resultMap

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
        return deal;
    }

    @GetMapping("/deal/{dealId}")
    public Product getDealProduct(
            @PathVariable("dealId") long dealId, ModelAndView modelAndView
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
    public String getSystemTime(@PathVariable("dealId") Long dealId){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String date = simpleDateFormat.format(new Date());

        return date;
    }

    @GetMapping("/deal/deductItem/{dealId}")
    public boolean deductItem(@PathVariable("dealId") Long dealId){
        return dealService.deductItemForDeal(dealId);
    }

    @GetMapping("/deal/lockItem/{dealId}")
    public boolean lockItem(@PathVariable("dealId") Long dealId){
        return dealService.lockItem(dealId);
    }
    @GetMapping("/deal/revertItem/{dealId}")
    public String revertItem(@PathVariable("dealId") Long dealId){
        dealService.revertItem(dealId);
        return null;
    }
}
