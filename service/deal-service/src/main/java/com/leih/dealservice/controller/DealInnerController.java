package com.leih.dealservice.controller;

import com.leih.dealservice.service.DealService;
import com.leih.shopping.common.result.ResultCodeEnum;
import com.leih.shopping.common.result.ResultData;
import com.leih.shopping.model.Deal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@RestController
@RequestMapping("/api/deal/inner")
public class DealInnerController {
    @Autowired
    DealService dealService;
    Logger logger = LoggerFactory.getLogger(DealController.class);

    @GetMapping("/deal/{dealId}")
    public ResultData<Deal> getDeal(@PathVariable("dealId") long dealId){

        return ResultData.success(dealService.getDealById(dealId));
    }

    @DeleteMapping("/deductItem/{dealId}")
    public ResultData<Boolean> deductItem(@PathVariable("dealId") Long dealId){
        boolean result = dealService.deductItemForDeal(dealId);
        return ResultData.success(result);
    }

    @PutMapping("/lockItem/{dealId}")
    public ResultData<Boolean> lockItem(@PathVariable("dealId") Long dealId){
        boolean result = dealService.lockItem(dealId);
        return ResultData.success(result);
    }
    @PutMapping("/revertItem/{dealId}")
    public ResultData<Object> revertItem(@PathVariable("dealId") Long dealId){
        dealService.revertItem(dealId);
        return ResultData.success(null);
    }


    /***
     * This method is used to add deal
     * @param name
     * @param productId
     * @param originalPrice
     * @param dealPrice
     * @param dealQuantity
     * @return
     * @throws ParseException
     */
    @PostMapping("/addDealAction")
    public ResultData<Deal> addDealAction(
            @RequestParam("name") String name,
            @RequestParam("productId") long productId,
            @RequestParam("originalPrice") BigDecimal originalPrice,
            @RequestParam("dealPrice") BigDecimal dealPrice,
            @RequestParam("dealQuantity") long dealQuantity,
            @RequestParam("dealStatus") int dealStatus
//            @RequestParam("startTime") String startTime,
//            @RequestParam("endTime") String endTime

    ) throws ParseException {
//        startTime = startTime.substring(0, 10) +  startTime.substring(11);
//        endTime = endTime.substring(0, 10) +  endTime.substring(11);
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-ddhh:mm");
        Deal deal = new Deal();
        deal.setName(name);
        deal.setProductId(productId);
        deal.setDealPrice(dealPrice);
        deal.setOriginalPrice(originalPrice);
        deal.setTotalStock(dealQuantity);
        deal.setAvailableStock((int) dealQuantity);
        deal.setLockStock(0L);
        deal.setDealStatus(dealStatus);
//        deal.setStartTime(format.parse(startTime));
//        deal.setEndTime(format.parse(endTime));
        //insert created deal into the database
        dealService.insertDeal(deal);

        return ResultData.success(deal);
    }

    @GetMapping("/deals")
    public ResultData<List<Deal>> getAllDeals(){

        return ResultData.success(dealService.getAllDeals());
    }
    @PostMapping("/changeDealStatus")
    public ResultData<Deal> changeDealStatus(@RequestParam long dealId, @RequestParam int dealStatus){
        Deal deal = dealService.getDealById(dealId);
        if(deal!=null){
            deal.setDealStatus(dealStatus);
            dealService.changeDealStatus(deal);
            return ResultData.success(deal);
        }else{
            return ResultData.fail(ResultCodeEnum.DEAL_NO_EXIST);
        }
    }

}
