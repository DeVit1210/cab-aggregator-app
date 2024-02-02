package com.modsen.payment.service.impl;

import com.modsen.payment.dto.request.CreditCardRequest;
import com.modsen.payment.dto.response.CreditCardListResponse;
import com.modsen.payment.dto.response.CreditCardResponse;
import com.modsen.payment.enums.Role;
import com.modsen.payment.exception.PaymentEntityNotFoundException;
import com.modsen.payment.mapper.CreditCardMapper;
import com.modsen.payment.model.CreditCard;
import com.modsen.payment.repository.CreditCardRepository;
import com.modsen.payment.service.CreditCardService;
import com.modsen.payment.service.StripeCustomerService;
import com.modsen.payment.service.StripeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreditCardServiceImpl implements CreditCardService {
    private final CreditCardRepository creditCardRepository;
    private final CreditCardMapper creditCardMapper;
    private final StripeService stripeService;
    private final StripeCustomerService stripeCustomerService;

    @Override
    public CreditCardResponse createCreditCard(CreditCardRequest request) {
        CreditCard creditCard = creditCardMapper.toCreditCard(request);
        String tokenForCreditCard = stripeService.createTokenForCreditCard(request);
        creditCard.setStripeId(tokenForCreditCard);
        if (request.isDefault()) {
            String stripeCustomerId = stripeCustomerService.getCustomerId(creditCard.getCardHolderId());
            stripeService.setDefaultCreditCard(stripeCustomerId, tokenForCreditCard);
        }
        CreditCard savedCreditCard = creditCardRepository.save(creditCard);

        return creditCardMapper.toCreditCardResponse(savedCreditCard);
    }

    @Override
    public CreditCardListResponse findAllCreditCards(Long cardHolderId, Role role) {
        List<CreditCard> creditCardList = creditCardRepository.findAllByCardHolderIdAndRole(cardHolderId, role);
        List<CreditCardResponse> creditCardListResponse = creditCardMapper.toCreditCardListResponse(creditCardList);

        return CreditCardListResponse.of(creditCardListResponse);
    }

    @Override
    public CreditCardResponse findCardById(Long cardId) {
        return creditCardRepository.findById(cardId)
                .map(creditCardMapper::toCreditCardResponse)
                .orElseThrow(() -> new PaymentEntityNotFoundException(cardId, CreditCard.class));
    }

    @Override
    public CreditCardResponse setDefaultCreditCard(Long cardId) {
        CreditCard creditCard = creditCardRepository.findById(cardId)
                .orElseThrow(() -> new PaymentEntityNotFoundException(cardId, CreditCard.class));
        String stripeCustomerId = stripeCustomerService.getCustomerId(creditCard.getCardHolderId());
        stripeService.setDefaultCreditCard(stripeCustomerId, creditCard.getStripeId());

        return creditCardMapper.toCreditCardResponse(creditCard);
    }

    @Override
    public CreditCard findCreditCardByStripeId(String cardStripeId) {
        return creditCardRepository.findByStripeId(cardStripeId)
                .orElseThrow(() -> new PaymentEntityNotFoundException(cardStripeId, CreditCard.class));
    }

    @Override
    public CreditCard findCreditCardById(Long creditCardId) {
        return creditCardRepository.findById(creditCardId)
                .orElseThrow(() -> new PaymentEntityNotFoundException(creditCardId, CreditCard.class));
    }
}
