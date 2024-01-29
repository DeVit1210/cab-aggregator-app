package com.modsen.driver.repository;

import com.modsen.driver.enums.DriverStatus;
import com.modsen.driver.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    Optional<Driver> findFirstByStatus(DriverStatus status);

    Optional<Driver> findFirstByStatusAndIdIsNotIn(DriverStatus status, List<Long> driversToExcludeIdList);

}
