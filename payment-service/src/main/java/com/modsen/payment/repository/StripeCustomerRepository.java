package com.modsen.payment.repository;

import com.modsen.payment.model.StripeCustomer;
import lombok.Lombok;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StripeCustomerRepository extends JpaRepository<StripeCustomer, Long> {
    Optional<StripeCustomer> findByPassengerId(Long passengerId);
}
