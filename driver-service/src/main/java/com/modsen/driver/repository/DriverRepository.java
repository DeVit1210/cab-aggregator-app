package com.modsen.driver.repository;

import com.modsen.driver.enums.DriverStatus;
import com.modsen.driver.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    Optional<Driver> findFirstByDriverStatus(DriverStatus status);

    Optional<Driver> findFirstByDriverStatusAndIdIsNotIn(DriverStatus status, List<Long> driversToExcludeIdList);

    int countAllByDriverStatus(DriverStatus driverStatus);
}
