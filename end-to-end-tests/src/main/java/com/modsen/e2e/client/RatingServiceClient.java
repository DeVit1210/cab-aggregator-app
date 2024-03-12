package com.modsen.e2e.client;

import com.modsen.e2e.dto.request.RatingRequest;
import com.modsen.e2e.dto.response.RatingListResponse;
import com.modsen.e2e.dto.response.RatingResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@FeignClient(
        name = "${feign.client.rating.name}",
        path = "${feign.client.rating.path}"
)
public interface RatingServiceClient {
    @GetMapping
    RatingListResponse getAllRatingsForPerson(@RequestParam Long ratedPersonId, @RequestParam String role);

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    RatingResponse createRating(@RequestBody RatingRequest request);
}
