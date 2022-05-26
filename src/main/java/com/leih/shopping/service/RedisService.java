package com.leih.shopping.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Collections;

@Service
public class RedisService {
    @Autowired
    JedisPool jedisPool;
    Logger logger= LoggerFactory.getLogger(RedisService.class);
    /***
     * Set value
     * @param key
     * @param value
     */
    public void setValue(String key, Long value){
        Jedis jedisClient = jedisPool.getResource();
        jedisClient.set(key,value.toString());
        jedisClient.close();
    }

    /***
     * Query a value in terms of a key
     * @param key
     * @return
     */
    public String getValue(String key){
        Jedis jedisClient = jedisPool.getResource();
        String value = jedisClient.get(key);
        jedisClient.close();
        return value;
    }

    /***
     * This method uses Lua script(in-built toolkit) to isolate the operations of querying and updating the database.
     * Every time, deduct one item.
     * This method ensures there's no products oversold.
     * @param key
     * @return
     */
    public boolean deductItemFromRedisValidator(String key){
        try{
            Jedis jedisClient = jedisPool.getResource();
        /*
            if redis.call('exists',KEYS[1]) == 1 then
                local stock = tonumber(redis.call('get', KEYS[1]))
                if( stock <=0 ) then
                    return -1
                end;
                redis.call('decr',KEYS[1]);
                return stock - 1;
            end;
            return -1;
        */
            String script = "if redis.call('exists',KEYS[1]) == 1 then\n" +
                    "                 local stock = tonumber(redis.call('get', KEYS[1]))\n" +
                    "                 if( stock <=0 ) then\n" +
                    "                    return -1\n" +
                    "                 end;\n" +
                    "                 redis.call('decr',KEYS[1]);\n" +
                    "                 return stock - 1;\n" +
                    "             end;\n" +
                    "             return -1;";
            Long stock = (Long) jedisClient.eval(script, Collections.singletonList(key), Collections.emptyList());
            if(stock<0){
                System.out.println("redis check stock: out of stock");
                return false;
            }else{
                System.out.println("redis check stock: success");
                return true;
            }
        }catch (Throwable throwable){
            System.out.println("redis check stock Failed：" + throwable);
            return false;
        }

    }

    public boolean checkUserLimits(Long dealId,Long userId){
        Jedis jedisClient = jedisPool.getResource();
        Boolean sismember = jedisClient.sismember("user_limits_deal_" + dealId, String.valueOf(userId));
        jedisClient.close();
        if(sismember)
            logger.info("The user: "+userId+" is in the restricted list");
        return sismember;
    }
    public void addUserLimits(Long dealId,Long userId){
        Jedis jedisClient = jedisPool.getResource();
        jedisClient.sadd("user_limits_deal_" + dealId, String.valueOf(userId));
        jedisClient.close();
    }
    public void removeUserLimits(Long dealId,Long userId){
        Jedis jedisClient = jedisPool.getResource();
        jedisClient.srem("user_limits_deal_" + dealId, String.valueOf(userId));
        jedisClient.close();
    }
}
