package com.cloud.hotel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"com.cloud.hotel", "com.cloud.common"})
@EnableDiscoveryClient
public class HotelApplication {
    public static void main(String[] args) {
        SpringApplication.run(HotelApplication.class, args);
    }
}
