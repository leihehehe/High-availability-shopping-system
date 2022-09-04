package com.leih.dealscan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableFeignClients
@ComponentScan({"com.leih.dealscan","com.leih.commonutil"})
public class DealScanApplication {

    public static void main(String[] args) {
        SpringApplication.run(DealScanApplication.class, args);
    }

}
