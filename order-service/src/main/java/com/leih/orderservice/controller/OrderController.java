package com.leih.orderservice.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.leih.orderservice.model.Deal;
import com.leih.orderservice.model.Order;
import com.leih.orderservice.service.OrderService;
import com.leih.orderservice.service.RedisService;
import org.aspectj.weaver.ast.Or;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class OrderController {
    Logger logger = LoggerFactory.getLogger(OrderController.class);
    @Autowired
    OrderService orderService;
    @Autowired
    RedisService redisService;
    @Autowired
    RestTemplate restTemplate;
    /***
     * This method is to show product and deal information
     * @param userId user id
     * @param dealId deal id
     * @param modelAndView model and view
     * @return model and view
     */
    @GetMapping("/checkout/{userId}/{dealId}")
    public Deal checkout(@PathVariable("userId") long userId, @PathVariable("dealId") long dealId, ModelAndView modelAndView){
        String dealUrl="http://localhost:8083/deal/"+dealId;
        Deal deal = restTemplate.getForObject(dealUrl, Deal.class);

        //Product product = productService.getProductById(deal.getProductId());

        return deal;
    }


    @GetMapping("/order/create/{userId}/{dealId}")
    public ModelAndView createOrder(@PathVariable("userId") long userId, @PathVariable("dealId") long dealId, ModelAndView modelAndView){
        boolean userLimits = redisService.checkUserLimits(dealId, userId);
        if(userLimits){
            modelAndView.addObject("resultInfo","Sorry, you cannot buy this product again.");
            modelAndView.setViewName("order_failed");
        }else {
            //check the number of available items
            if(orderService.stockValidator(dealId)){
                //create an order
                Order order = orderService.createOrder(dealId, userId);
                logger.info("Order created:"+order.getOrderNo());
                //add the user to restricted list
                redisService.addUserLimits(dealId,userId);
                logger.info("The user :"+userId+" has been added to the restricted list.");
                modelAndView.addObject("order",order);
                modelAndView.setViewName("pay_order");
            }else{
                modelAndView.addObject("resultInfo","Sorry, the item is out of stock.");
                modelAndView.setViewName("order_failed");
            }
        }

        return modelAndView;
    }

    @GetMapping("/order/pay/{orderNo}")
    public Order payOrder(@PathVariable("orderNo") String orderNo){
        Order order = orderService.processPayment(orderNo);
        return order;
    }


    @GetMapping("/orderQuery/{orderNo}")
    public Order viewOrder(@PathVariable("orderNo") String orderNo){
        Order order = orderService.getOrderByNo(orderNo);
        return order;
    }
}
