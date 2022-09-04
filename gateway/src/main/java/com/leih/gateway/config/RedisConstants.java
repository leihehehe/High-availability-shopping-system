package com.leih.gateway.config;

/**
 * Redis常量配置类
 * set name admin
 */
public class RedisConstants {

    public static final String STOCK_PREFIX = "stock:";
    public static final String USER_LIMIT_PREFIX = "user_limits_deal_";
    //单位：秒
    public static final long STOCK_TIMEOUT = 20 * 60;


    public static final String STOCK_BLOOM_FILTER="stock:bloom:filter";

    public static final String USER_LOGIN_KEY_PREFIX = "user:login:";
    //    public static final String userinfoKey_suffix = ":info";
    public static final int USERKEY_TIMEOUT = 60 * 60 * 24 * 7;


}
