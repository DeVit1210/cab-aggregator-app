package com.modsen.rating.repository;

import com.modsen.rating.enums.Role;
import com.modsen.rating.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long>, JpaSpecificationExecutor<Rating> {
    boolean existsByRoleAndRideId(Role role, Long rideId);

    List<Rating> findAllByRoleAndRatedPersonId(Role role, Long ratedPersonId);
}
