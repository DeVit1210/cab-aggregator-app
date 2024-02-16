package com.modsen.promocode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class PromocodeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PromocodeServiceApplication.class, args);
    }

}
