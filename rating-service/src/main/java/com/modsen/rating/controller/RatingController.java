package com.modsen.rating.controller;

import com.modsen.rating.constants.ControllerMappings;
import com.modsen.rating.dto.request.PageSettingRequest;
import com.modsen.rating.dto.request.RatingRequest;
import com.modsen.rating.dto.response.AverageRatingListResponse;
import com.modsen.rating.dto.response.AverageRatingResponse;
import com.modsen.rating.dto.response.PagedRatingResponse;
import com.modsen.rating.dto.response.RatingListResponse;
import com.modsen.rating.dto.response.RatingResponse;
import com.modsen.rating.enums.RatingValue;
import com.modsen.rating.enums.Role;
import com.modsen.rating.service.RatingService;
import com.modsen.rating.validation.EnumValue;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ControllerMappings.RATING_CONTROLLER)
@RequiredArgsConstructor
public class RatingController {
    private final RatingService ratingService;

    @GetMapping
    public RatingListResponse getAllRatingsForPerson(@RequestParam Long ratedPersonId,
                                                     @RequestParam @EnumValue(enumClass = Role.class) String role) {
        return ratingService.getAllRatings(ratedPersonId, Role.valueOf(role));
    }

    @GetMapping("/page")
    public PagedRatingResponse getRatingsForPerson(@RequestParam Long ratedPersonId,
                                                   @RequestParam @EnumValue(enumClass = Role.class) String role,
                                                   PageSettingRequest request) {
        return ratingService.getRatings(ratedPersonId, Role.valueOf(role), request);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RatingResponse createRating(@Valid @RequestBody RatingRequest request) {
        return ratingService.createRating(request);
    }

    @GetMapping("/average/{ratedPersonId}")
    public AverageRatingResponse getAverageRating(@PathVariable Long ratedPersonId,
                                                  @RequestParam @EnumValue(enumClass = Role.class) String role) {
        return ratingService.getAverageRating(ratedPersonId, Role.valueOf(role));
    }

    @GetMapping("/average")
    public AverageRatingListResponse getAllAverageRatings(@RequestParam @EnumValue(enumClass = Role.class) String role) {
        return ratingService.getAllAverageRatings(Role.valueOf(role));
    }

    @GetMapping("/{ratingId}")
    private RatingResponse getRatingById(@PathVariable Long ratingId) {
        return ratingService.getRatingById(ratingId);
    }

    @PatchMapping("/{ratingId}")
    private RatingResponse updateRating(@PathVariable Long ratingId,
                                        @RequestParam @EnumValue(enumClass = RatingValue.class) String ratingValue) {
        return ratingService.updateRating(ratingId, RatingValue.valueOf(ratingValue));
    }
}
