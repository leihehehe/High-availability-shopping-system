package com.leih.orderservice.controller;

import com.leih.commonutil.api.DealApi;
import com.leih.orderservice.service.OrderService;
import com.leih.orderservice.service.RedisService;
import com.leih.commonutil.model.Deal;
import com.leih.commonutil.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.leih.commonutil.util.ResultData;
import com.leih.commonutil.util.ReturnCode;

@RestController
public class OrderController {
    Logger logger = LoggerFactory.getLogger(OrderController.class);
    @Autowired
    OrderService orderService;
    @Autowired
    RedisService redisService;
    @Autowired
    DealApi dealApi;
    /***
     * This method is to show product and deal information
     * @param userId user id
     * @param dealId deal id
     * @return com.leih.commonutil.model and view
     */
    @GetMapping("/checkout/{userId}/{dealId}")
    public ResultData<Deal> checkout(@PathVariable("userId") long userId, @PathVariable("dealId") long dealId){

        return ResultData.success(dealApi.getDealById(dealId).getData());
    }


    @PostMapping("/order/create/")
    public ResultData<Order> createOrder(@RequestParam("userId") long userId, @RequestParam("dealId") long dealId){
        boolean userLimits = redisService.checkUserLimits(dealId, userId);
        if(userLimits){
            return ResultData.fail(ReturnCode.USER_LIMITED.getCode(), "You are limited to buy this item.");
        }else {
            //check the number of available items
            if(orderService.stockValidator(dealId)){
                //create an order
                ResultData<Order> orderResultData = orderService.createOrder(dealId, userId);
                Order order = orderResultData.getData();
                if(order!=null){
                    logger.info("Order created:"+order.getOrderNo());
                    //add the user to restricted list
                    redisService.addUserLimits(dealId,userId);
                    logger.info("The user :"+userId+" has been added to the restricted list.");
                }
                return orderResultData;
            }else{
                //item is out of stock
                return ResultData.fail(ReturnCode.OUT_OF_STOCK.getCode(), "Sorry, the item has been sold out.");

            }
        }

    }

    @GetMapping("/order/pay/{orderNo}")
    public ResultData<Order> payOrder(@PathVariable("orderNo") String orderNo){

        return ResultData.success(orderService.processPayment(orderNo));
    }


    @GetMapping("/order/{orderNo}")
    public ResultData<Order> viewOrder(@PathVariable("orderNo") String orderNo){
        return ResultData.success(orderService.getOrderByNo(orderNo));
    }
}
