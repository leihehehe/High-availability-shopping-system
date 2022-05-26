package com.leih.shopping;

import com.leih.shopping.db.dao.DealDao;
import com.leih.shopping.db.mappers.DealMapper;
import com.leih.shopping.db.mappers.ProductMapper;
import com.leih.shopping.db.po.Deal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
public class DaoTest {
    @Autowired
    private DealMapper dealMapper;
    @Autowired
    private DealDao dealDao;
    @Autowired
    private ProductMapper productMapper;
    @Test
    void activityTest() {
        Deal deal = new Deal();
        deal.setName("test11");
        deal.setProductId(999L);
        deal.setTotalStock(100L);
        deal.setDealPrice(new BigDecimal(99));
        deal.setDealStatus(1);
        deal.setOriginalPrice(new BigDecimal(99));
        deal.setAvailableStock(100);
        deal.setLockStock(0L);
        dealMapper.insert(deal);
        System.out.println("====>>>>" + dealMapper.selectByPrimaryKey(1L));
    }

    @Test
    void setSeckillActivityQuery() {
        List<Deal> activities = dealDao.queryDealsByStatus(0);
        System.out.println(activities.size());
        activities.stream().forEach(deal -> System.out.println(deal.toString()));
    }
}
