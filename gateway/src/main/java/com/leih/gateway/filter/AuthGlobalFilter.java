//package com.leih.gateway.filter;
//
//import com.alibaba.fastjson.JSONObject;
//import com.leih.gateway.config.RedisConstants;
//import com.leih.shopping.common.result.ResultCodeEnum;
//import com.leih.shopping.common.result.ResultData;
//import com.leih.shopping.common.util.IpUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.buffer.DataBuffer;
//import org.springframework.http.HttpCookie;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.http.server.reactive.ServerHttpResponse;
//import org.springframework.stereotype.Component;
//import org.springframework.util.AntPathMatcher;
//import org.springframework.util.CollectionUtils;
//import org.springframework.util.MultiValueMap;
//import org.springframework.util.StringUtils;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//import redis.clients.jedis.Jedis;
//import redis.clients.jedis.JedisPool;
//
//import java.nio.charset.StandardCharsets;
//import java.util.List;
//
//@Component
//public class AuthGlobalFilter implements GlobalFilter {
//    AntPathMatcher matcher = new AntPathMatcher();
//    @Autowired
//    JedisPool jedisPool;
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        //get request
//        ServerHttpRequest request = exchange.getRequest();
//        //get response
//        ServerHttpResponse response = exchange.getResponse();
//        String path = request.getURI().getPath();
//        if(matcher.match("/api/**/inner/**",path)){
//            return out(response,ResultCodeEnum.PERMISSION);
//        }
//        String userId=this.getUserId(request);
//        if(userId==null) request.mutate();
//
//        return chain.filter(exchange);
//    }
//
//    private Mono<Void> out(ServerHttpResponse response, ResultCodeEnum permission) {
//
//
//
//        ResultData<Object> build = ResultData.build(null, permission);
//
//        byte[] bytes = JSONObject.toJSONString(build).getBytes(StandardCharsets.UTF_8);
//
//        DataBuffer wrap = response.bufferFactory().wrap(bytes);
//        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
//        return response.writeWith(Mono.just(wrap));
//    }
//
//    public String getUserId(ServerHttpRequest request){
//        String token=null;
//        //get token from header
//        List<String> tokenList = request.getHeaders().get("token");
//        if(!CollectionUtils.isEmpty(tokenList)){
//            token = tokenList.get(0);
//        }
//        //get token from cookie
//        if(token==null){
//            MultiValueMap<String, HttpCookie> cookies = request.getCookies();
//            HttpCookie cookie = cookies.getFirst("token");
//            if(cookie!=null){
//                token=cookie.getValue();
//            }
//        }
//
//        //redis
//        if(!StringUtils.isEmpty(token)){
//            Jedis resource = jedisPool.getResource();
//
//            String strJson = resource.get(RedisConstants.USER_LOGIN_KEY_PREFIX + token);
//            JSONObject jsonObject = JSONObject.parseObject(strJson);
//
//            return jsonObject.getString("userId");
//        }
//        return null;
//
//    }
//}
