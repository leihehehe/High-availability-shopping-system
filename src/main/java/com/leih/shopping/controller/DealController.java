package com.leih.shopping.controller;

import com.leih.shopping.db.dao.DealDao;
import com.leih.shopping.db.dao.ProductDao;
import com.leih.shopping.db.po.Deal;
import com.leih.shopping.db.po.Product;
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

@Controller
public class DealController {
    @Autowired
    DealDao dealDao;
    @Autowired
    ProductDao productDao;
    @GetMapping("/addDeal")
    public String addDeal(){
        return "add_deal";
    }
    @GetMapping("/")
    public String dealList(Map<String,Object> resultMap){
        List<Deal> deals = dealDao.queryDealsByStatus(1);
        resultMap.put("deals",deals);
        return "index";
    }

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
    @PostMapping("/addDealAction")
    public String addDealAction(
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
        dealDao.insertDeal(deal);
        //return deal instance to the view
        resultMap.put("deal", deal);
        return "add_success";
    }

    @GetMapping("/item/{dealId}")
    public ModelAndView dealProduct(
            @PathVariable("dealId") long dealId, ModelAndView modelAndView
    ){
        Deal deal = dealDao.queryDealById(dealId);
        Product product = productDao.queryProductById(deal.getProductId());
        modelAndView.addObject("deal",deal);
        modelAndView.addObject("product",product);
        modelAndView.setViewName("deal_product");
        return modelAndView;
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
//        Deal deal = dealDao.queryDealById(dealId);
//        long currentDatetime = new Date().getTime();
//        long startDatetime = deal.getStartTime().getTime();

        return date;
    }

}
