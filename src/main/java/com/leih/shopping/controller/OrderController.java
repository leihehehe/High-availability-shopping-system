package com.leih.shopping.controller;

import com.leih.shopping.db.po.Deal;
import com.leih.shopping.db.po.Order;
import com.leih.shopping.db.po.Product;
import com.leih.shopping.service.DealService;
import com.leih.shopping.service.OrderService;
import com.leih.shopping.service.ProductService;
import com.leih.shopping.service.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class OrderController {
    Logger logger = LoggerFactory.getLogger(OrderController.class);
    @Autowired
    OrderService orderService;
    @Autowired
    DealService dealService;
    @Autowired
    ProductService productService;
    @Autowired
    RedisService redisService;
    /***
     * This method is to show product and deal information
     * @param userId user id
     * @param dealId deal id
     * @param modelAndView model and view
     * @return model and view
     */
    @GetMapping("/checkout/{userId}/{dealId}")
    public ModelAndView checkout(@PathVariable("userId") long userId, @PathVariable("dealId") long dealId, ModelAndView modelAndView){
        modelAndView.setViewName("checkout");
        Deal deal = dealService.getDealById(dealId);
        Product product = productService.getProductById(deal.getProductId());
        modelAndView.addObject("deal",deal);
        modelAndView.addObject("product",product);
        return modelAndView;
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
    public ModelAndView payOrder(@PathVariable("orderNo") String orderNo, ModelAndView modelAndView){
        Order order = orderService.processPayment(orderNo);
        modelAndView.addObject("order",order);
        modelAndView.setViewName("order_result");
        return modelAndView;
    }


    @GetMapping("/orderQuery/{orderNo}")
    public ModelAndView viewOrder(@PathVariable("orderNo") String orderNo,ModelAndView modelAndView){
        Order order = orderService.getOrderByNo(orderNo);
        modelAndView.addObject("order",order);
        modelAndView.setViewName("order_result");
        return modelAndView;
    }
}
