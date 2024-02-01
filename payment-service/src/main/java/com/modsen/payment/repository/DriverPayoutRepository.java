package com.modsen.payment.repository;

import com.modsen.payment.model.DriverPayout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverPayoutRepository extends JpaRepository<DriverPayout, Long> {

}
