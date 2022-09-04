package com.leih.dealscan.component;

import com.leih.commonutil.api.DealApi;
import com.leih.commonutil.model.Deal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

import java.util.List;

@Component
public class RedisInitializationRunner implements ApplicationRunner {
    @Autowired
    DealApi dealApi;
    @Autowired
    JedisPool jedisPool;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Jedis jedisClient = jedisPool.getResource();
        List<Deal> deals = dealApi.getDeals().getData();
        for(Deal deal:deals){
            long availableStock = (long) deal.getAvailableStock();
            jedisClient.set("stock:"+deal.getId(),Long.toString(availableStock), SetParams.setParams().ex(600));//expire in 600 seconds->10 mins
        }
    }

}
