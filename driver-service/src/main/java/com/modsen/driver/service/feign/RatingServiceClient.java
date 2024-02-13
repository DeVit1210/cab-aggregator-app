package com.modsen.driver.service.feign;

import com.modsen.driver.dto.response.AverageRatingListResponse;
import com.modsen.driver.dto.response.AverageRatingResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("RATING-SERVICE")
public interface RatingServiceClient {
    @GetMapping("/api/v1/ratings/average")
    AverageRatingListResponse findAllAverageRatings(@RequestParam String role);

    @GetMapping("/api/v1/ratings/average/{ratedPersonId}")
    AverageRatingResponse findAverageRating(@PathVariable Long ratedPersonId, @RequestParam String role);
}
