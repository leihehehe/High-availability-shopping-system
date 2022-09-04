package com.leih.dealservice;

import com.leih.dealservice.service.DealService;
import com.leih.shopping.common.constant.RedisConstants;
import com.leih.shopping.model.Deal;
import org.mybatis.spring.annotation.MapperScan;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

import java.util.List;

@SpringBootApplication
@MapperScan(basePackages = {"com.leih.dealservice.mapper"})
@EnableFeignClients
@ComponentScan({"com.leih.dealservice","com.leih.shopping.common"})
public class DealServiceApplication implements CommandLineRunner {
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private DealService dealService;
    Logger log = LoggerFactory.getLogger(DealServiceApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(DealServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        //get bloomFilter
        RBloomFilter<Object> stringRBloomFilter = redissonClient.getBloomFilter(RedisConstants.STOCK_BLOOM_FILTER);
        //initialize -> around 10 items, error: 0.0001
        stringRBloomFilter.tryInit(10,0.0001);
        //add all deals to the bloom filter
        List<Deal> deals = dealService.getAllDeals();
        for (Deal deal : deals){
            stringRBloomFilter.add(deal.getId());
        }
        log.info("Bloom Filter has been initialized");
    }
}
