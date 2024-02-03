package com.modsen.rating.service.impl;

import com.modsen.rating.dto.request.PageSettingRequest;
import com.modsen.rating.dto.request.RatingRequest;
import com.modsen.rating.dto.response.AverageRatingResponse;
import com.modsen.rating.dto.response.PagedRatingResponse;
import com.modsen.rating.dto.response.RatingListResponse;
import com.modsen.rating.dto.response.RatingResponse;
import com.modsen.rating.enums.RatingValue;
import com.modsen.rating.enums.Role;
import com.modsen.rating.exception.RatingAlreadyExistsException;
import com.modsen.rating.exception.RatingNotFoundException;
import com.modsen.rating.mapper.RatingMapper;
import com.modsen.rating.model.Rating;
import com.modsen.rating.repository.RatingRepository;
import com.modsen.rating.service.RatingService;
import com.modsen.rating.utils.PageRequestUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {
    private final RatingRepository ratingRepository;
    private final RatingMapper ratingMapper;
    @Value("${rating.average.default}")
    private double defaultAverageRating;

    @Override
    public RatingResponse createRating(RatingRequest request) {
        validateRatingRequest(request);
        Rating rating = ratingMapper.toRating(request);
        Rating savedRating = ratingRepository.save(rating);

        return ratingMapper.toRatingResponse(savedRating);
    }

    @Override
    public RatingListResponse getAllRatings(Long ratedPersonId, Role role) {
        List<Rating> ratingList = ratingRepository.findAllByRoleAndRatedPersonId(role, ratedPersonId);
        if (ratingList.isEmpty()) {
            return new RatingListResponse(Collections.emptyList(), defaultAverageRating);
        }
        double averageRating = calculateAverageRating(ratingList);
        List<RatingResponse> ratingResponseList = ratingMapper.toRatingListResponse(ratingList);

        return new RatingListResponse(ratingResponseList, averageRating);
    }

    @Override
    public AverageRatingResponse getAverageRating(Long ratedPersonId, Role role) {
        List<Rating> ratingList = ratingRepository.findAllByRoleAndRatedPersonId(role, ratedPersonId);
        double averageRating = ratingList.isEmpty()
                ? defaultAverageRating
                : calculateAverageRating(ratingList);

        return AverageRatingResponse.builder()
                .ratedPersonId(ratedPersonId)
                .role(role)
                .averageRating(averageRating)
                .build();
    }

    @Override
    public PagedRatingResponse getRatings(Long ratedPersonId, Role role, PageSettingRequest request) {
        PageRequest pageRequest = PageRequestUtils.makePageRequest(request);
        Specification<Rating> searchSpecification = (root, query, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("ratedPersonId"), ratedPersonId),
                        criteriaBuilder.equal(root.get("role"), role.name())
                );
        Page<Rating> ratingPage = ratingRepository.findAll(searchSpecification, pageRequest);
        PageRequestUtils.validatePageResponse(pageRequest, ratingPage);

        return ratingMapper.toPagedRatingResponse(ratingPage);
    }

    @Override
    public RatingResponse getRatingById(Long ratingId) {
        return ratingRepository.findById(ratingId)
                .map(ratingMapper::toRatingResponse)
                .orElseThrow(() -> new RatingNotFoundException(ratingId));
    }

    @Override
    public RatingResponse updateRating(Long ratingId, RatingValue ratingValue) {
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new RatingNotFoundException(ratingId));
        rating.setRatingValue(ratingValue);
        Rating updatedRating = ratingRepository.save(rating);

        return ratingMapper.toRatingResponse(updatedRating);
    }

    private void validateRatingRequest(RatingRequest request) {
        Role role = Role.valueOf(request.getRole());
        Long rideId = request.getRideId();
        if (ratingRepository.existsByRoleAndRideId(role, rideId)) {
            throw new RatingAlreadyExistsException(role, rideId);
        }
    }

    private double calculateAverageRating(List<Rating> ratingList) {
        return ratingList.stream()
                .map(Rating::getRatingValue)
                .collect(Collectors.averagingDouble(RatingValue::getValue));
    }
}
