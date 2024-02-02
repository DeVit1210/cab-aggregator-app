package com.modsen.payment.repository;

import com.modsen.payment.model.DriverAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverAccountRepository extends JpaRepository<DriverAccount, Long> {
    Optional<DriverAccount> findByDriverId(Long driverId);
}
