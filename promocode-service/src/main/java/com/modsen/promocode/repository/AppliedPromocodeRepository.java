package com.modsen.promocode.repository;

import com.modsen.promocode.enums.ApplianceStatus;
import com.modsen.promocode.model.AppliedPromocode;
import com.modsen.promocode.model.Promocode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppliedPromocodeRepository extends JpaRepository<AppliedPromocode, Long> {
    boolean existsByPromocodeAndPassengerId(Promocode promocode, Long passengerId);

    int countAllByPromocode(Promocode promocode);

    Optional<AppliedPromocode> findByPassengerIdAndApplianceStatus(Long passengerId, ApplianceStatus applianceStatus);
}