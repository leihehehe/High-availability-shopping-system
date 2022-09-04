package com.leih.gateway.config;

import com.leih.gateway.filter.AuthenticationConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.stereotype.Component;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain webFilterChain(ServerHttpSecurity httpSecurity, ReactiveAuthenticationManager reactiveAuthenticationManager, ServerAuthenticationConverter converter){
        return httpSecurity.cors().and()
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .authorizeExchange()
                .anyExchange().authenticated()
                .and()
                .addFilterAt(new CorsFilter(), SecurityWebFiltersOrder.SECURITY_CONTEXT_SERVER_WEB_EXCHANGE)

                .addFilterAt(getAuthenticationWebFilter(reactiveAuthenticationManager, converter), SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    //customized authentication filter
    public AuthenticationWebFilter getAuthenticationWebFilter(ReactiveAuthenticationManager authenticationManager, ServerAuthenticationConverter authenticationConverter){
        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(authenticationManager);
        authenticationWebFilter.setServerAuthenticationConverter(authenticationConverter);
//        authenticationWebFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/admin/*"));
        return authenticationWebFilter;
    }

    @Bean
    public ServerAuthenticationConverter getAuthenticationConverter(){
        return new AuthenticationConverter();
    }

    @Bean
    ReactiveAuthenticationManager getReactiveAuthenticationManager(){
        return Mono::just;
    }
}
