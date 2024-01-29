package com.modsen.driver.repository;

import com.modsen.driver.model.DriverWithSuggestedRide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriverWithSuggestedRideRepository extends JpaRepository<DriverWithSuggestedRide, Long> {
    List<DriverWithSuggestedRide> findAllBySuggestedRideId(Long suggestedRideId);
}
