package com.leih.orderservice.service;

import com.leih.shopping.common.constant.RedisConstants;
import com.leih.shopping.model.Deal;
import org.redisson.RedissonBloomFilter;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

import java.util.Collections;

@Service
public class RedisService {
    @Autowired
    JedisPool jedisPool;
    @Autowired
    RedissonClient redissonClient;
    @Autowired
    DealApi dealApi;
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
    public boolean deductItemFromRedisValidator(String key,long dealId){
        try{
            Jedis jedisClient = jedisPool.getResource();
            if(jedisClient.get(key)==null){
                Deal deal = dealApi.getDealById(dealId).getData();
                logger.info("Deal in redis expired, store the deal stock info into Redis");
                jedisClient.setex(key, RedisConstants.STOCK_TIMEOUT,Long.toString(deal.getAvailableStock()));//expire in 1200 seconds->20 mins
            }
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
                logger.info("redis check stock: out of stock");
                return false;
            }else{
                logger.info("redis check stock: success");
                return true;
            }
        }catch (Throwable throwable){
            logger.error("redis check stock Failed：" + throwable);
            return false;
        }

    }

    public boolean checkUserLimits(Long dealId,Long userId){
        Jedis jedisClient = jedisPool.getResource();
        Boolean sismember = jedisClient.sismember(RedisConstants.USER_LIMIT_PREFIX + dealId, String.valueOf(userId));
        jedisClient.close();
        if(sismember)
            logger.info("The user: "+userId+" is in the restricted list");
        return sismember;
    }
    public void addUserLimits(Long dealId,Long userId){
        Jedis jedisClient = jedisPool.getResource();
        jedisClient.sadd(RedisConstants.USER_LIMIT_PREFIX+ dealId, String.valueOf(userId));
        jedisClient.close();
    }
    public void removeUserLimits(Long dealId,Long userId){
        Jedis jedisClient = jedisPool.getResource();
        jedisClient.srem(RedisConstants.USER_LIMIT_PREFIX+ dealId, String.valueOf(userId));
        jedisClient.close();
    }

    /***
     * Set redis distributed lock
     * @param lockKey key
     * @param requestId who wants to create this lock
     * @param expireTime expire time
     * @return true or false
     */
    public boolean setLock(String lockKey,String requestId, int expireTime) {
        Jedis jedisClient = jedisPool.getResource();
        //NX: if key no exist,then set otherwise do nothing
        //PX: set expire time
        SetParams setParams = SetParams.setParams().px(expireTime).nx();
        String result = jedisClient.set(lockKey, requestId,setParams);
        return "OK".equals(result);
    }

    /***
     * remove lock
     * @param lockKey lock key
     * @param requestId who created this lock
     * @return true or false
     */
    public boolean removeLock(String lockKey,String requestId){

        Jedis jedisClient = jedisPool.getResource();
        //check if the requestId stored in the redis equal to the requestId passed in.
        String script = "if redis.call('get', KEYS[1] == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end)";
        Long result = (Long) jedisClient.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));
        return result == 1L;
    }
}
