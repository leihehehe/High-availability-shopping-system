package com.leih.shopping;

import com.leih.shopping.service.RedisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RedisTest {
    @Autowired
    RedisService redisService;
    @Test
    public void setNumberOfItems(){
        redisService.setValue("stock:19",999L);
        System.out.println("set the number of items");
    }
    @Test
    public void getNumberOfItems(){
        String stock = redisService.getValue("stock:19");
        System.out.println(stock);
    }

    @Test
    public void deductStock() {
        redisService.deductItemFromRedisValidator("stock:19");
        String stock = redisService.getValue("stock:19");
        System.out.println(stock);
    }
}
