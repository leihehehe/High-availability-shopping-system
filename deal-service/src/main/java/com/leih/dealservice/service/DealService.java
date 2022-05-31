package com.leih.dealservice.service;

import com.leih.dealservice.dao.DealDao;
import com.leih.dealservice.model.Deal;
import com.leih.dealservice.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class DealService {
    @Autowired
    DealDao dealDao;
    @Autowired
    RestTemplate restTemplate;
    public Deal getDealById(long dealId){
        return dealDao.queryDealById(dealId);
    }
    public boolean deductItemForDeal(long dealId){
        return dealDao.deductItem(dealId);
    }
    public void insertDeal(Deal deal){
        dealDao.insertDeal(deal);
    }
    public Product getProduct(long dealId){
        Deal deal = getDealById(dealId);

        String url = "http://localhost:8086/product/"+deal.getProductId();
        return restTemplate.getForObject(url,Product.class);
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
