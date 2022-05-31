package com.leih.dealscan.component;

import com.leih.dealscan.model.Deal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;

@Component
public class RedisInitializationRunner implements ApplicationRunner {
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    JedisPool jedisPool;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Jedis jedisClient = jedisPool.getResource();
        String url = "http://localhost:8083/deal";
        ResponseEntity<Deal[]> responseEntity = restTemplate.getForEntity(url,Deal[].class);
        Deal[] deals = responseEntity.getBody();

        for(Deal deal:deals){
            long availableStock = (long) deal.getAvailableStock();
            jedisClient.set("stock:"+deal.getId(), Long.toString(availableStock));
        }
    }

}
