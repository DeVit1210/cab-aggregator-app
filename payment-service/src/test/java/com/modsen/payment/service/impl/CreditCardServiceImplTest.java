package com.modsen.payment.service.impl;

import com.modsen.payment.constants.MessageTemplates;
import com.modsen.payment.constants.TestConstants;
import com.modsen.payment.dto.request.CreditCardRequest;
import com.modsen.payment.dto.response.CreditCardListResponse;
import com.modsen.payment.dto.response.CreditCardResponse;
import com.modsen.payment.enums.Role;
import com.modsen.payment.exception.CardIsNotDefaultException;
import com.modsen.payment.exception.CreditCardAlreadyExistsException;
import com.modsen.payment.exception.DefaultCreditCardMissingException;
import com.modsen.payment.exception.PaymentEntityNotFoundException;
import com.modsen.payment.mapper.CreditCardMapperImpl;
import com.modsen.payment.model.CreditCard;
import com.modsen.payment.repository.CreditCardRepository;
import com.modsen.payment.service.StripeCustomerService;
import com.modsen.payment.service.StripeService;
import com.modsen.payment.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreditCardServiceImplTest {
    private final Long creditCardId = TestConstants.CREDIT_CARD_ID;
    @Mock
    private CreditCardRepository creditCardRepository;
    @Mock
    private CreditCardMapperImpl creditCardMapper;
    @Mock
    private StripeService stripeService;
    @Mock
    private StripeCustomerService stripeCustomerService;
    @InjectMocks
    private CreditCardServiceImpl creditCardService;

    @Test
    void findCardById_CreditCardExists_ReturnCreditCard() {
        CreditCard creditCard = TestUtils.defaultCreditCard();

        when(creditCardRepository.findById(anyLong()))
                .thenReturn(Optional.of(creditCard));
        when(creditCardMapper.toCreditCardResponse(any(CreditCard.class)))
                .thenCallRealMethod();

        CreditCardResponse actualCreditCard = creditCardService.findCardById(creditCardId);

        assertNotNull(actualCreditCard);
        verify(creditCardRepository).findById(creditCardId);
        verify(creditCardMapper).toCreditCardResponse(creditCard);
    }

    @Test
    void findCardById_CreditCardDoesNotExist_ThrowPaymentEntityNotFoundId() {
        String exceptionMessage = String.format(
                MessageTemplates.ENTITY_NOT_FOUND_BY_ID.getValue(),
                CreditCard.class.getSimpleName(),
                creditCardId
        );

        when(creditCardRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> creditCardService.findCardById(creditCardId))
                .isInstanceOf(PaymentEntityNotFoundException.class)
                .hasMessage(exceptionMessage);
    }

    @Test
    void findAllCreditCardsForPerson_AtLeastOneExist_ReturnCreditCardList() {
        Role role = Role.PASSENGER;
        Long cardHolderId = TestConstants.CARD_HOLDER_ID;
        List<CreditCard> creditCardList =
                Collections.nCopies(3, TestUtils.creditCardWithRoleAndIsDefault(role, false));

        when(creditCardRepository.findAllByCardHolderIdAndRole(anyLong(), any(Role.class)))
                .thenReturn(creditCardList);
        when(creditCardMapper.toCreditCardListResponse(anyList()))
                .thenCallRealMethod();

        CreditCardListResponse actualCreditCardList = creditCardService.findAllCreditCards(cardHolderId, role.name());

        assertEquals(creditCardList.size(), actualCreditCardList.quantity());
        verify(creditCardRepository).findAllByCardHolderIdAndRole(cardHolderId, role);
        verify(creditCardMapper).toCreditCardListResponse(creditCardList);
    }

    @Test
    void findAllCreditCardsForPerson_NoExistingCards_ReturnEmptyList() {
        Role role = Role.PASSENGER;
        Long cardHolderId = TestConstants.CARD_HOLDER_ID;

        when(creditCardRepository.findAllByCardHolderIdAndRole(anyLong(), any(Role.class)))
                .thenReturn(Collections.emptyList());
        when(creditCardMapper.toCreditCardListResponse(anyList()))
                .thenCallRealMethod();

        CreditCardListResponse actualCreditCardList = creditCardService.findAllCreditCards(cardHolderId, role.name());

        assertTrue(actualCreditCardList.creditCards().isEmpty());
        verify(creditCardRepository).findAllByCardHolderIdAndRole(cardHolderId, role);
    }

    @Test
    void setDefaultCreditCard_ValidCardId_ShouldMakeCreditCardDefault() {
        CreditCard creditCard = TestUtils.defaultCreditCard();
        creditCard.setDefault(false);
        String stripeCardId = TestConstants.Stripe.CREDIT_CARD_ID;
        String stripeCustomerId = TestConstants.Stripe.CUSTOMER_ID;
        Long passengerId = TestConstants.PASSENGER_ID;

        when(creditCardRepository.findById(anyLong()))
                .thenReturn(Optional.of(creditCard));
        when(stripeCustomerService.getCustomerId(anyLong()))
                .thenReturn(stripeCustomerId);
        doNothing().when(stripeService)
                .setDefaultCreditCard(anyString(), anyString());
        when(creditCardRepository.save(any(CreditCard.class)))
                .thenReturn(creditCard);
        when(creditCardMapper.toCreditCardResponse(any(CreditCard.class)))
                .thenCallRealMethod();

        CreditCardResponse actualCreditCard = creditCardService.setDefaultCreditCard(creditCardId);

        assertNotNull(actualCreditCard);
        assertTrue(creditCard.isDefault());
        verify(creditCardRepository).findById(creditCardId);
        verify(stripeCustomerService).getCustomerId(passengerId);
        verify(stripeService).setDefaultCreditCard(stripeCustomerId, stripeCardId);
        verify(creditCardRepository).save(creditCard);
        verify(creditCardMapper).toCreditCardResponse(creditCard);
    }

    @Test
    void setDefaultCreditCard_CardIsAlreadyDefault_ShouldReturnCreditCard() {
        CreditCard creditCard = TestUtils.defaultCreditCard();
        creditCard.setDefault(true);

        when(creditCardRepository.findById(anyLong()))
                .thenReturn(Optional.of(creditCard));
        when(creditCardMapper.toCreditCardResponse(any(CreditCard.class)))
                .thenCallRealMethod();

        CreditCardResponse actualCreditCard = creditCardService.setDefaultCreditCard(creditCardId);

        assertNotNull(actualCreditCard);
        assertTrue(creditCard.isDefault());
        verify(creditCardRepository).findById(creditCardId);
        verify(creditCardMapper).toCreditCardResponse(creditCard);
        verify(stripeCustomerService, never()).getCustomerId(anyLong());
        verify(stripeService, never()).setDefaultCreditCard(anyString(), anyString());
        verify(creditCardRepository, never()).save(creditCard);
    }

    @Test
    void getDefaultCreditCard_DefaultCardExists_ShouldReturnDefaultCreditCard() {
        Long cardHolderId = TestConstants.CARD_HOLDER_ID;
        CreditCard creditCard = TestUtils.defaultCreditCard();

        when(creditCardRepository.findByCardHolderIdAndIsDefaultIsTrue(anyLong()))
                .thenReturn(Optional.of(creditCard));
        when(creditCardMapper.toCreditCardResponse(any(CreditCard.class)))
                .thenCallRealMethod();

        CreditCard actualCreditCard = creditCardService.getDefaultCreditCard(cardHolderId);

        assertNotNull(actualCreditCard);
        verify(creditCardRepository).findByCardHolderIdAndIsDefaultIsTrue(cardHolderId);
        verify(creditCardMapper).toCreditCardResponse(creditCard);
    }

    @Test
    void getDefaultCreditCard_DefaultCardDoesNotExist_ThrowDefaultCreditCardMissingException() {
        Long cardHolderId = TestConstants.CARD_HOLDER_ID;
        String exceptionMessage = String.format(MessageTemplates.CREDIT_CARD_MISSING.getValue(), cardHolderId);

        when(creditCardRepository.findByCardHolderIdAndIsDefaultIsTrue(anyLong()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> creditCardService.getDefaultCreditCard(cardHolderId))
                .isInstanceOf(DefaultCreditCardMissingException.class)
                .hasMessage(exceptionMessage);
        verify(creditCardRepository).findByCardHolderIdAndIsDefaultIsTrue(cardHolderId);
    }

    @Test
    void findCreditCardByStripeId_CardExists_ShouldReturnCreditCard() {
        String stripeCardId = TestConstants.Stripe.CREDIT_CARD_ID;
        CreditCard creditCard = TestUtils.defaultCreditCard();

        when(creditCardRepository.findByStripeId(anyString()))
                .thenReturn(Optional.of(creditCard));

        CreditCard actualCreditCard = creditCardService.findCreditCardByStripeId(stripeCardId);

        assertEquals(creditCard, actualCreditCard);
        verify(creditCardRepository).findByStripeId(stripeCardId);
    }

    @Test
    void findCreditCardByStripeId_CardDoesNotExist_ThrowPaymentEntityNotFoundException() {
        String stripeCardId = TestConstants.Stripe.CREDIT_CARD_ID;
        String exceptionMessage = String.format(
                MessageTemplates.ENTITY_NOT_FOUND_BY_STRIPE_ID.getValue(),
                CreditCard.class.getSimpleName(),
                stripeCardId
        );

        when(creditCardRepository.findByStripeId(anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> creditCardService.findCreditCardByStripeId(stripeCardId))
                .isInstanceOf(PaymentEntityNotFoundException.class)
                .hasMessage(exceptionMessage);
    }

    @Test
    void createCreditCard_FirstCardForPassengerAndDefault_ShouldCreateCreditCardAndSetDefaultInStripe() {
        Long cardHolderId = TestConstants.CARD_HOLDER_ID;
        Role role = Role.PASSENGER;
        String stripeCardId = TestConstants.Stripe.CREDIT_CARD_ID;
        String stripeCustomerId = TestConstants.Stripe.CUSTOMER_ID;
        CreditCard creditCard = TestUtils.creditCardWithRoleAndIsDefault(role, true);
        CreditCardRequest creditCardRequest = TestUtils.creditCardRequestWithRoleAndIsDefault(role, true);

        when(creditCardRepository.findAllByCardHolderIdAndRole(anyLong(), any(Role.class)))
                .thenReturn(Collections.emptyList());
        when(creditCardMapper.toCreditCard(any(CreditCardRequest.class)))
                .thenReturn(creditCard);
        when(stripeService.createTokenForCreditCard(any(CreditCardRequest.class)))
                .thenReturn(stripeCardId);
        when(stripeCustomerService.getCustomerId(anyLong()))
                .thenReturn(TestConstants.Stripe.CUSTOMER_ID);
        doNothing().when(stripeService)
                .setDefaultCreditCard(anyString(), anyString());
        when(creditCardRepository.save(any(CreditCard.class)))
                .thenReturn(creditCard);
        when(creditCardMapper.toCreditCardResponse(any(CreditCard.class)))
                .thenCallRealMethod();

        CreditCardResponse createdCreditCard = creditCardService.createCreditCard(creditCardRequest);

        assertNotNull(createdCreditCard);
        verify(creditCardRepository).findAllByCardHolderIdAndRole(cardHolderId, role);
        verify(creditCardMapper).toCreditCard(creditCardRequest);
        verify(stripeService).createTokenForCreditCard(creditCardRequest);
        verify(stripeCustomerService).getCustomerId(creditCardRequest.getCardHolderId());
        verify(stripeService).setDefaultCreditCard(stripeCustomerId, stripeCardId);
        verify(creditCardRepository).save(creditCard);
        verify(creditCardMapper).toCreditCardResponse(creditCard);
    }

    @Test
    void createCreditCard_FirstCardForPassengerAndNotDefault_ThrowCardIsNotDefaultException() {
        CreditCardRequest creditCardRequest = TestUtils.creditCardRequestWithRoleAndIsDefault(Role.PASSENGER, false);
        String exceptionMessage = String.format(
                MessageTemplates.DEFAULT_CREDIT_CARD.getValue(),
                creditCardRequest.getNumber(),
                creditCardRequest.getRole(),
                creditCardRequest.getCardHolderId()
        );

        when(creditCardRepository.findAllByCardHolderIdAndRole(anyLong(), any(Role.class)))
                .thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> creditCardService.createCreditCard(creditCardRequest))
                .isInstanceOf(CardIsNotDefaultException.class)
                .hasMessage(exceptionMessage);

        verify(creditCardRepository).findAllByCardHolderIdAndRole(creditCardRequest.getCardHolderId(), Role.PASSENGER);
        verify(stripeService, never()).createTokenForCreditCard(creditCardRequest);
        verify(stripeCustomerService, never()).getCustomerId(anyLong());
        verify(stripeService, never()).setDefaultCreditCard(anyString(), anyString());
        verify(creditCardRepository, never()).save(any(CreditCard.class));
    }

    @Test
    void createCreditCard_ValidRequestForDriver_ShouldCreateCardAndNotRegisterInStripe() {
        Long cardHolderId = TestConstants.CARD_HOLDER_ID;
        Role role = Role.DRIVER;
        CreditCard creditCard = TestUtils.creditCardWithRoleAndIsDefault(role, true);
        CreditCardRequest creditCardRequest = TestUtils.creditCardRequestWithRoleAndIsDefault(role, true);

        when(creditCardRepository.findAllByCardHolderIdAndRole(anyLong(), any(Role.class)))
                .thenReturn(Collections.emptyList());
        when(creditCardMapper.toCreditCard(any(CreditCardRequest.class)))
                .thenReturn(creditCard);
        when(stripeService.createTokenForCreditCard(any(CreditCardRequest.class)))
                .thenReturn(TestConstants.Stripe.CREDIT_CARD_ID);
        when(creditCardRepository.save(any(CreditCard.class)))
                .thenReturn(creditCard);
        when(creditCardMapper.toCreditCardResponse(any(CreditCard.class)))
                .thenCallRealMethod();

        CreditCardResponse createdCreditCard = creditCardService.createCreditCard(creditCardRequest);

        assertNotNull(createdCreditCard);
        verify(creditCardRepository).findAllByCardHolderIdAndRole(cardHolderId, role);
        verify(creditCardMapper).toCreditCard(creditCardRequest);
        verify(stripeService).createTokenForCreditCard(creditCardRequest);
        verify(stripeCustomerService, never()).getCustomerId(anyLong());
        verify(stripeService, never()).setDefaultCreditCard(anyString(), anyString());
        verify(creditCardRepository).save(creditCard);
        verify(creditCardMapper).toCreditCardResponse(creditCard);
    }

    @Test
    void createCreditCard_DuplicateCardNumber_ThrowCreditCardAlreadyExistsException() {
        CreditCard creditCard = TestUtils.defaultCreditCard();
        CreditCardRequest creditCardRequest = TestUtils.defaultCreditCardRequest();
        String exceptionMessage = String.format(
                MessageTemplates.CREDIT_CARD_ALREADY_EXISTS.getValue(),
                creditCardRequest.getNumber(),
                creditCardRequest.getRole(),
                creditCardRequest.getCardHolderId()
        );

        when(creditCardRepository.findAllByCardHolderIdAndRole(anyLong(), any(Role.class)))
                .thenReturn(Collections.singletonList(creditCard));

        assertThatThrownBy(() -> creditCardService.createCreditCard(creditCardRequest))
                .isInstanceOf(CreditCardAlreadyExistsException.class)
                .hasMessage(exceptionMessage);
        verify(stripeService, never()).createTokenForCreditCard(any(CreditCardRequest.class));
        verify(creditCardRepository, never()).save(any(CreditCard.class));
    }
}