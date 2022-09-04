//package com.leih.gateway.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.reactive.CorsWebFilter;
//import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
//import org.springframework.web.util.pattern.PathPatternParser;
//
//@Configuration
//public class CorsConfig {
//    @Bean
//    public CorsWebFilter corsFilter(){
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowCredentials(Boolean.TRUE);
//        config.addAllowedMethod("*");
//        config.addAllowedOriginPattern("*");
//        config.addAllowedHeader("*");
//        config.setMaxAge(3600L);
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
//        source.registerCorsConfiguration("/**",config);
//        return new CorsWebFilter(source);
//    }
//}
