package com.leih.shopping.service;

import com.leih.shopping.db.dao.DealDao;
import com.leih.shopping.db.po.Deal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DealService {
    @Autowired
    DealDao dealDao;
    public Deal getDealById(long dealId){
        return dealDao.queryDealById(dealId);
    }
}
