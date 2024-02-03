package com.modsen.payment.repository;

import com.modsen.payment.model.DriverAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverAccountRepository extends JpaRepository<DriverAccount, Long> {
}
