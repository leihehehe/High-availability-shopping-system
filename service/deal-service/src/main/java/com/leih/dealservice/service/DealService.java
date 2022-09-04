package com.leih.dealservice.service;

import com.leih.dealservice.dao.DealDao;
import com.leih.shopping.common.constant.RedisConstants;
import com.leih.shopping.common.result.ResultCodeEnum;
import com.leih.shopping.common.result.ResultData;
import com.leih.shopping.model.Deal;
import com.leih.shopping.model.Product;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DealService {
    @Autowired
    DealDao dealDao;
    @Autowired
    ProductApi productApi;
    @Autowired
    RedissonClient redissonClient;
    Logger logger = LoggerFactory.getLogger(DealService.class);
    public List<Deal> getAllDeals(){
        return dealDao.getAllDeals();
    }
    public Deal getDealById(long dealId){
        if (!checkDealExists(dealId)) return null;
        return dealDao.queryDealById(dealId);
    }
    public boolean deductItemForDeal(long dealId){
        return dealDao.deductItem(dealId);
    }
    public void insertDeal(Deal deal){
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(RedisConstants.STOCK_BLOOM_FILTER);
        bloomFilter.add(deal.getId());
        dealDao.insertDeal(deal);
    }

    @CircuitBreaker(name = "dealService",fallbackMethod = "serviceDownFallBack")
    public ResultData<Product> getProduct(long dealId){
        if (!checkDealExists(dealId)) {
            return null;
        }
        Deal deal = getDealById(dealId);
        ResultData<Product> resultData = productApi.getProduct(deal.getProductId());
        return resultData;
    }

    public ResultData<Product> serviceDownFallBack(Exception exception){
        logger.info("product service is down");
        return ResultData.fail(ResultCodeEnum.SERVICE_ERROR);
    }

    private boolean checkDealExists(long dealId) {
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(RedisConstants.STOCK_BLOOM_FILTER);
        if(bloomFilter.contains(dealId)){
            return true;
        }else{
            logger.info("Bloom Filter - deal id does not exist");
            return false;
        }
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

    public void changeDealStatus(Deal deal){
        dealDao.updateDeal(deal);
    }
}
