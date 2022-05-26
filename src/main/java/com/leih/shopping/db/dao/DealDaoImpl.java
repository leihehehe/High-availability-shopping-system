package com.leih.shopping.db.dao;

import com.leih.shopping.db.mappers.DealMapper;
import com.leih.shopping.db.po.Deal;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DealDaoImpl implements DealDao {

    Logger logger = LoggerFactory.getLogger(DealDaoImpl.class);
    @Autowired
    private DealMapper dealMapper;

    @Override
    public List<Deal> queryDealsByStatus(int activityStatus) {
        return dealMapper.queryDealsByStatus(activityStatus);
    }

    @Override
    public void insertDeal(Deal deal) {
        dealMapper.insert(deal);
    }

    @Override
    public Deal queryDealById(long activityId) {
        return dealMapper.selectByPrimaryKey(activityId);
    }

    @Override
    public void updateDeal(Deal deal) {
        dealMapper.updateByPrimaryKey(deal);
    }

    @Override
    public boolean lockStock(Long dealId) {
        int result = dealMapper.lockStock(dealId);
        if(result<1){
            logger.info("lock item failed");
            return false;
        }
        return true;
    }

    @Override
    public boolean deductItem(Long dealId) {
        int result = dealMapper.deductStock(dealId);
        if(result<1){
            logger.info("deducting item failed, the item is out of stock");
            return false;
        }else{
            return true;
        }
    }

    @Override
    public void revertBackStock(Long dealId) {
        Deal deal = queryDealById(dealId);
        deal.setLockStock(deal.getLockStock()-1);
        deal.setAvailableStock(deal.getAvailableStock()+1);
        updateDeal(deal);
    }
}
