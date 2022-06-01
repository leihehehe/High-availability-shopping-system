package com.leih.dealservice.service;

import com.leih.commonutil.api.ProductApi;
import com.leih.commonutil.util.ReturnCode;
import com.leih.dealservice.dao.DealDao;
import com.leih.commonutil.model.Deal;
import com.leih.commonutil.model.Product;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.leih.commonutil.util.ResultData;

import java.util.List;

@Service
public class DealService {
    @Autowired
    DealDao dealDao;
    @Autowired
    ProductApi productApi;
    Logger logger = LoggerFactory.getLogger(DealService.class);
    public Deal getDealById(long dealId){
        return dealDao.queryDealById(dealId);
    }
    public boolean deductItemForDeal(long dealId){
        return dealDao.deductItem(dealId);
    }
    public void insertDeal(Deal deal){
        dealDao.insertDeal(deal);
    }

    @CircuitBreaker(name = "dealService",fallbackMethod = "serviceDownFallBack")
    public ResultData<Product> getProduct(long dealId){
        Deal deal = getDealById(dealId);
        ResultData<Product> resultData = productApi.getProduct(deal.getProductId());
        return resultData;
    }

    public ResultData<Product> serviceDownFallBack(Exception exception){
        logger.info("product service is down");
        return ResultData.fail(ReturnCode.RC500.getCode(),"The system is down");
    }
    public List<Deal> getDealByStatus(int activityStatus){
        return dealDao.queryDealsByStatus(activityStatus);
    }
    public boolean lockItem(long dealId){
        return dealDao.lockStock(dealId);
    }
    public void revertItem(long dealId){
        dealDao.revertBackStock(dealId);
    }
}
