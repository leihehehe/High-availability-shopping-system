package com.leih.dealservice.dao;

import com.leih.shopping.model.Deal;

import java.util.List;

public interface DealDao {

    public List<Deal> queryDealsByStatus(int activityStatus);
    public List<Deal> getAllDeals();

    public void insertDeal(Deal deal);

    public Deal queryDealById(long activityId);

    public void updateDeal(Deal deal);
    public boolean lockStock(Long dealId);
    public boolean deductItem(Long dealId);
    public void revertBackStock(Long dealId);
}
