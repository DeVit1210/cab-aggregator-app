package com.modsen.passenger.service.feign;

import com.modsen.passenger.config.FeignConfig;
import com.modsen.passenger.constants.ServiceMappings;
import com.modsen.passenger.dto.response.AverageRatingListResponse;
import com.modsen.passenger.dto.response.AverageRatingResponse;
import com.modsen.passenger.service.feign.fallback.RatingServiceClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = ServiceMappings.RATING_SERVICE,
        configuration = FeignConfig.class,
        url = ServiceMappings.RATING_BASE_URL,
        fallback = RatingServiceClientFallback.class
)
public interface RatingServiceClient {
    @GetMapping(ServiceMappings.ALL_AVERAGE_RATINGS_URL)
    AverageRatingListResponse findAllAverageRatings(@RequestParam String role);

    @GetMapping(ServiceMappings.AVERAGE_RATING_BY_ID_URL)
    AverageRatingResponse findAverageRating(@PathVariable Long ratedPersonId, @RequestParam String role);
}