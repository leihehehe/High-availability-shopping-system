package com.leih.userservice.service;

import com.leih.shopping.common.result.ResultData;
import com.leih.shopping.model.Deal;
import com.leih.shopping.model.Product;
import com.leih.shopping.model.User;
import com.leih.userservice.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestParam;

import javax.xml.transform.Result;
import java.math.BigDecimal;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    DealApi dealApi;
    @Autowired
    ProductApi productApi;
    /**
     * User login
     * @param username
     * @param password
     * @return
     */

    public User login(String username, String password) {
        //select *from userinfo where login_name=? and passwd=?

        //encode password
        String newPass = DigestUtils.md5DigestAsHex(password.getBytes());
        User userInfo = userMapper.getUserByUsernamePasswd(username, newPass);

        return userInfo;
    }

    public ResultData<List<Deal>> getAllDeals(){
        ResultData<List<Deal>> allDeals = dealApi.getAllDeals();
        return allDeals;
    }
    public ResultData<List<Product>> getAllProducts(){
        ResultData<List<Product>> allProducts = productApi.getAllProducts();
        return allProducts;
    }

    public ResultData<Deal> addDealAction( String name, long productId, BigDecimal originalPrice,  BigDecimal dealPrice, long dealQuantity, int dealStatus){
        ResultData<Deal> deal = dealApi.addDealAction(name,productId,originalPrice,dealPrice,dealQuantity,dealStatus);
        return deal;
    }

    public ResultData<Deal> changeDealStatus(String dealId,String status){
        return dealApi.changeDealStatus(Long.parseLong(dealId), Integer.parseInt(status));
    }
}
