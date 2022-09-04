package com.leih.userservice.controller;

import com.alibaba.fastjson.JSONObject;
import com.leih.shopping.common.constant.RedisConstants;
import com.leih.shopping.common.result.ResultCodeEnum;
import com.leih.shopping.common.result.ResultData;
import com.leih.shopping.common.util.IpUtil;
import com.leih.shopping.model.Deal;
import com.leih.shopping.model.User;
import com.leih.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpCookie;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    JedisPool jedisPool;
    @PostMapping("/login")
    public ResultData login(@RequestParam("username") String username, @RequestParam("password") String password, HttpServletResponse response){

        User user=  userService.login(username,password);

        if(user!=null){

            //generate token
            String token = UUID.randomUUID().toString().replaceAll("-", "");

            //store to redis
            Jedis resource = jedisPool.getResource();
            resource.setex(RedisConstants.USER_LOGIN_KEY_PREFIX+token,RedisConstants.USERKEY_TIMEOUT, String.valueOf(user.getId()));

            //return username and token
            Map<String,Object> resultMap=new HashMap<>();
            resultMap.put("username",user.getUsername());
            resultMap.put("token",token);
            return ResultData.success(resultMap);

        }else{

            return ResultData.fail(ResultCodeEnum.USERNAME_PASSWORD_INVALID);
        }

    }
    @GetMapping("/logout")
    public ResultData logout(HttpServletRequest request){
        Jedis resource = jedisPool.getResource();
        String token = request.getHeader("token");
        if(token!=null){
            resource.del(RedisConstants.USER_LOGIN_KEY_PREFIX+token);
            return ResultData.success(ResultCodeEnum.SUCCESS);
        }
        return ResultData.fail(ResultCodeEnum.FAIL);
    }

    @GetMapping("/deals")
    public ResultData viewAllDeals(){
        return userService.getAllDeals();
    }

    @GetMapping("/products")
    public ResultData viewAllProducts(){
        return userService.getAllProducts();
    }

    @PostMapping("/addDeal")
    public ResultData<Deal> addDealAction(
            @RequestParam("name") String name,
            @RequestParam("productId") String productId,
            @RequestParam("originalPrice") String originalPrice,
            @RequestParam("dealPrice") String dealPrice,
            @RequestParam("dealQuantity") String dealQuantity,
            @RequestParam("dealStatus") String dealStatus
    ){
        try{
            ResultData<Deal> dealResultData = userService.addDealAction(name, Long.parseLong(productId), new BigDecimal(originalPrice), new BigDecimal(dealPrice), Long.parseLong(dealQuantity), Integer.parseInt(dealStatus));
            return dealResultData;
        }catch (Exception exception){
            return ResultData.fail(ResultCodeEnum.FAIL);
        }
    }

    @PostMapping("/changeDealStatus")
    public ResultData<Deal> changeDealStatus(@RequestParam String dealId, @RequestParam String dealStatus) {
        return userService.changeDealStatus(dealId,dealStatus);
    }

}
