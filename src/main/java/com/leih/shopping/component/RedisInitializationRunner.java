package com.leih.shopping.component;

import com.leih.shopping.db.dao.DealDao;
import com.leih.shopping.db.po.Deal;
import com.leih.shopping.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RedisInitializationRunner implements ApplicationRunner {
    @Autowired
    DealDao dealDao;
    @Autowired
    RedisService redisService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<Deal> deals = dealDao.queryDealsByStatus(1);
        for(Deal deal:deals){
            redisService.setValue("stock:"+deal.getId(),(long)deal.getAvailableStock());
        }
    }
}
