package com.modsen.payment.repository;

import com.modsen.payment.model.DriverAccount;
import com.modsen.payment.model.DriverPayout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriverPayoutRepository extends JpaRepository<DriverPayout, Long> {
    List<DriverPayout> findAllByAccount(DriverAccount driverAccount);
}
