package com.modsen.rating.repository;

import com.modsen.rating.enums.Role;
import com.modsen.rating.model.Rating;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class RatingRepositoryTest {
    @Autowired
    private RatingRepository ratingRepository;

    private List<Rating> ratings;

    static Stream<Arguments> findAllByRoleArgumentsProvider() {
        return Stream.of(
                Arguments.of(Role.DRIVER, 1),
                Arguments.of(Role.PASSENGER, 2)
        );
    }

    @BeforeEach
    void setUp() {
        ratings = List.of(
                Rating.builder()
                        .role(Role.PASSENGER)
                        .ratedPersonId(1L)
                        .rideId(1L)
                        .build(),
                Rating.builder()
                        .role(Role.DRIVER)
                        .ratedPersonId(1L)
                        .rideId(1L)
                        .build(),
                Rating.builder()
                        .role(Role.PASSENGER)
                        .ratedPersonId(2L)
                        .rideId(2L)
                        .build()
        );
        ratingRepository.saveAll(ratings);
    }

    @AfterEach
    void tearDown() {
        ratingRepository.deleteAll(ratings);
    }

    @Test
    void existsByRoleAndRideId_FoundAtLeastOne_ReturnTrue() {
        boolean actualResult = ratingRepository.existsByRoleAndRideId(Role.PASSENGER, 1L);
        assertTrue(actualResult);
    }

    @Test
    void existsByRoleAndRideId_NotFound_ReturnFalse() {
        boolean actualResult = ratingRepository.existsByRoleAndRideId(Role.PASSENGER, 3L);
        assertFalse(actualResult);
    }

    @Test
    void findAllByRoleAndRatedPersonId_ExistsAtLeastOne_ReturnRatings() {
        int expectedSize = 1;

        List<Rating> ratingList = ratingRepository.findAllByRoleAndRatedPersonId(Role.DRIVER, 1L);

        assertEquals(expectedSize, ratingList.size());
    }

    @Test
    void findAllByRoleAndRatedPersonId_DoesNotExist_ReturnEmptyList() {
        List<Rating> ratingList = ratingRepository.findAllByRoleAndRatedPersonId(Role.DRIVER, 3L);
        assertTrue(ratingList.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("findAllByRoleArgumentsProvider")
    void findAllByRole_ExistsAtLeastOne_ReturnRatings(Role role, int expectedSize) {
        List<Rating> allRatingsByRole = ratingRepository.findAllByRole(role);
        assertEquals(expectedSize, allRatingsByRole.size());
    }
}