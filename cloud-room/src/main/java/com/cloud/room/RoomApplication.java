package com.cloud.room;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"com.cloud.room", "com.cloud.common"})
@EnableDiscoveryClient
public class RoomApplication {
    public static void main(String[] args) {
        SpringApplication.run(RoomApplication.class, args);
    }
}
