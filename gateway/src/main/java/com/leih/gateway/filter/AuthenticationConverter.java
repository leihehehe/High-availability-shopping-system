package com.leih.gateway.filter;

import com.leih.gateway.config.RedisConstants;
import com.leih.shopping.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;

public class AuthenticationConverter implements ServerAuthenticationConverter {
    @Autowired
    JedisPool jedisPool;
    AntPathMatcher matcher = new AntPathMatcher();
    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        Jedis resource = jedisPool.getResource();
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        //inner api are not able to called.
        if(matcher.match("/api/**/inner/**",path)){
            return Mono.just(new UsernamePasswordAuthenticationToken(null, null));
        }
        //login request
        if(matcher.match("/user/login",path)){
            return Mono.just(new UsernamePasswordAuthenticationToken(null, null,null));
        }
        //all the requests that are not accessing admin page, just make then pass.
        if(!matcher.match("/user/**",path)){
            return Mono.just(new UsernamePasswordAuthenticationToken(null, null,null));
        }

        HttpHeaders headers = exchange.getRequest().getHeaders();
        String token=null;
        //get token from header
        List<String> tokens = headers.get("token");
        if(!CollectionUtils.isEmpty(tokens)){
             token=tokens.get(0);
        }
        //get token from cookie
        if(token==null){
            MultiValueMap<String, HttpCookie> cookies = request.getCookies();
            HttpCookie cookie = cookies.getFirst("token");
            if(cookie!=null){
                token=cookie.getValue();
            }
        }
        //if token is still null, need login
        if(token ==null) return Mono.just(new UsernamePasswordAuthenticationToken(null, null));
        //if token is not null, check user
        String userId = resource.get(RedisConstants.USER_LOGIN_KEY_PREFIX+token);
        if(userId!=null){
            //invalid token
            return Mono.just(new UsernamePasswordAuthenticationToken(null, null, null));
        }else{
            //valid token
            return Mono.just(new UsernamePasswordAuthenticationToken(null, null, null));
        }
    }
}
