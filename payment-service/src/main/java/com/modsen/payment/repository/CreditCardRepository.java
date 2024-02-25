package com.modsen.payment.repository;

import com.modsen.payment.enums.Role;
import com.modsen.payment.model.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {
    List<CreditCard> findAllByCardHolderIdAndRole(Long cardHolderId, Role role);

    Optional<CreditCard> findByStripeId(String stripeCardId);

    Optional<CreditCard> findByCardHolderIdAndIsDefaultIsTrue(Long cardHolderId);
}
