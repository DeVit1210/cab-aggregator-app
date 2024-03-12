package com.modsen.e2e;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class EndToEndTestsApplication {

    public static void main(String[] args) {
        SpringApplication.run(EndToEndTestsApplication.class, args);
    }

}
