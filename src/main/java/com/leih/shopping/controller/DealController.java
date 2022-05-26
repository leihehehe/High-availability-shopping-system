package com.leih.shopping.controller;

import com.leih.shopping.db.dao.DealDao;
import com.leih.shopping.db.dao.ProductDao;
import com.leih.shopping.db.po.Deal;
import com.leih.shopping.db.po.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        dealDao.insertDeal(deal);
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

}
