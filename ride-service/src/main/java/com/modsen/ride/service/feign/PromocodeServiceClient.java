package com.modsen.ride.service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient("PROMOCODE-SERVICE")
public class PromocodeServiceClient {
}
