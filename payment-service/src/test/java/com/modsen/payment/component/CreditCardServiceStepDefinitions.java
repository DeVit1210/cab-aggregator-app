package com.modsen.payment.component;

import com.modsen.payment.constants.TestConstants;
import com.modsen.payment.dto.request.CreditCardRequest;
import com.modsen.payment.dto.response.CreditCardListResponse;
import com.modsen.payment.dto.response.CreditCardResponse;
import com.modsen.payment.enums.Role;
import com.modsen.payment.mapper.CreditCardMapperImpl;
import com.modsen.payment.model.CreditCard;
import com.modsen.payment.repository.CreditCardRepository;
import com.modsen.payment.service.StripeCustomerService;
import com.modsen.payment.service.StripeService;
import com.modsen.payment.service.impl.CreditCardServiceImpl;
import com.modsen.payment.utils.TestUtils;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CreditCardServiceStepDefinitions {
    private final Long cardHolderId = TestConstants.CARD_HOLDER_ID;
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

    private CreditCard creditCard;
    private CreditCardRequest creditCardRequest;
    private CreditCardResponse creditCardResponse;
    private CreditCardListResponse creditCardListResponse;

    public CreditCardServiceStepDefinitions() {
        MockitoAnnotations.openMocks(this);
    }

    @Given("Valid request for credit card creating for {string}")
    public void validRequestForCreditCardCreatingFor(String roleName) {
        creditCardRequest = TestUtils.creditCardRequestWithRoleAndIsDefault(Role.valueOf(roleName), true);
        creditCard = TestUtils.creditCardWithRoleAndIsDefault(Role.valueOf(roleName), true);
    }

    @When("App user add credit card")
    public void businessLogicForCreditCardCreatingIsInvoked() {
        String stripeCardId = TestConstants.Stripe.CREDIT_CARD_ID;

        when(creditCardRepository.findAllByCardHolderIdAndRole(anyLong(), any()))
                .thenReturn(Collections.emptyList());
        when(creditCardMapper.toCreditCard(any()))
                .thenReturn(creditCard);
        when(stripeService.createTokenForCreditCard(any()))
                .thenReturn(stripeCardId);
        when(stripeCustomerService.getCustomerId(anyLong()))
                .thenReturn(TestConstants.Stripe.CUSTOMER_ID);
        doNothing().when(stripeService)
                .setDefaultCreditCard(anyString(), anyString());
        when(creditCardRepository.save(any()))
                .thenReturn(creditCard);
        when(creditCardMapper.toCreditCardResponse(any()))
                .thenCallRealMethod();

        creditCardResponse = creditCardService.createCreditCard(creditCardRequest);
    }

    @Then("Credit card response should be present and contain created credit card")
    public void creditCardResponseShouldBePresentAndContainCreatedCreditCard() {
        assertThat(creditCardResponse)
                .isNotNull();
        assertThat(creditCardResponse.cardHolderId())
                .isEqualTo(creditCardRequest.getCardHolderId());
        assertThat(creditCardResponse.number())
                .isEqualTo(creditCardRequest.getNumber());
    }

    @And("Methods needed to create passenger's credit card were called")
    public void methodsNeededToCreatePassengerSCreditCardWereCalled() {
        verify(creditCardRepository).findAllByCardHolderIdAndRole(cardHolderId, Role.PASSENGER);
        verify(creditCardMapper).toCreditCard(creditCardRequest);
        verify(stripeService).createTokenForCreditCard(creditCardRequest);
        verify(stripeCustomerService).getCustomerId(creditCardRequest.getCardHolderId());
        verify(stripeService).setDefaultCreditCard(anyString(), anyString());
        verify(creditCardRepository).save(creditCard);
        verify(creditCardMapper).toCreditCardResponse(creditCard);
    }

    @And("Methods needed to create driver's credit card were called")
    public void methodsNeededToCreateDriverSCreditCardWereCalled() {
        verify(creditCardRepository).findAllByCardHolderIdAndRole(cardHolderId, Role.DRIVER);
        verify(creditCardMapper).toCreditCard(creditCardRequest);
        verify(stripeService).createTokenForCreditCard(creditCardRequest);
        verify(stripeCustomerService, never()).getCustomerId(anyLong());
        verify(stripeService, never()).setDefaultCreditCard(anyString(), anyString());
        verify(creditCardRepository).save(creditCard);
        verify(creditCardMapper).toCreditCardResponse(creditCard);
    }

    @Given("{int} credit cards exist for person")
    public void creditCardsExistForPerson(int cardsQuantity) {
        List<CreditCard> creditCardList = Collections.nCopies(cardsQuantity, TestUtils.defaultCreditCard());

        when(creditCardRepository.findAllByCardHolderIdAndRole(anyLong(), any()))
                .thenReturn(creditCardList);
    }

    @When("App user search for all his credit cards")
    public void businessLogicToRetrieveCreditCardsForPersonIsInvoked() {
        Long cardHolderId = TestConstants.CARD_HOLDER_ID;

        when(creditCardMapper.toCreditCardListResponse(anyList()))
                .thenCallRealMethod();

        creditCardListResponse = creditCardService.findAllCreditCards(cardHolderId, Role.PASSENGER.name());
    }

    @Then("Response should contain {int} credit cards")
    public void responseShouldContainCreditCards(int expectedCardsQuantity) {
        assertThat(creditCardListResponse.creditCards())
                .hasSize(expectedCardsQuantity);
    }

    @And("Methods needed to retrieve all credit cards for person were called")
    public void methodsNeededToRetrieveAllCreditCardsForPersonWereCalled() {
        verify(creditCardRepository).findAllByCardHolderIdAndRole(cardHolderId, Role.PASSENGER);
        verify(creditCardMapper).toCreditCardListResponse(anyList());
    }

    @Given("No credit cards exist for person")
    public void noCreditCardsExistForPerson() {
        when(creditCardRepository.findAllByCardHolderIdAndRole(anyLong(), any()))
                .thenReturn(Collections.emptyList());
    }

    @Given("Current default card with id {long}")
    public void currentDefaultCardWithId(long currentDefaultCardId) {
        CreditCard currentDefaultCreditCard = TestUtils.creditCardWithRoleAndIsDefault(Role.PASSENGER, true);
        currentDefaultCreditCard.setId(currentDefaultCardId);

        when(creditCardRepository.findAllByCardHolderIdAndRole(anyLong(), any()))
                .thenReturn(Collections.singletonList(currentDefaultCreditCard));
    }

    @When("Passenger set new default card with id {long}")
    public void businessLogicToSetNewDefaultCardWithIdIsInvoked(long newDefaultCardId) {
        String stripeCustomerId = TestConstants.Stripe.CUSTOMER_ID;
        creditCard = TestUtils.creditCardWithRoleAndIsDefault(Role.PASSENGER, false);
        creditCard.setId(newDefaultCardId);

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

        creditCardResponse = creditCardService.setDefaultCreditCard(newDefaultCardId);
    }

    @Then("Response should contain credit card with id {long}")
    public void responseShouldContainCreditCardWithId(long expectedCardId) {
        assertThat(creditCardResponse)
                .isNotNull()
                .extracting(CreditCardResponse::id)
                .isEqualTo(expectedCardId);
        assertThat(creditCard.isDefault())
                .isTrue();
    }

    @And("Methods needed to set new default card were called")
    public void methodsNeededToSetNewDefaultCardWereCalled() {
        verify(creditCardRepository).findAllByCardHolderIdAndRole(cardHolderId, Role.PASSENGER);
        verify(creditCardRepository).findById(anyLong());
        verify(stripeCustomerService).getCustomerId(cardHolderId);
        verify(stripeService).setDefaultCreditCard(anyString(), anyString());
        verify(creditCardRepository).save(creditCard);
        verify(creditCardMapper).toCreditCardResponse(creditCard);
    }

    @Given("Credit card exists for person")
    public void creditCardExistsForPerson() {
        creditCard = TestUtils.creditCardWithRoleAndIsDefault(Role.PASSENGER, true);

        when(creditCardRepository.findAllByCardHolderIdAndRole(anyLong(), any()))
                .thenReturn(Collections.singletonList(creditCard));
    }

    @When("Passenger search for default credit card")
    public void businessLogicToRetrieveDefaultCreditCardIsInvoked() {
        creditCard = creditCardService.getDefaultCreditCard(cardHolderId);
    }

    @Then("Response should be present and contain default credit card")
    public void responseShouldBePresentAndContainDefaultCreditCard() {
        assertThat(creditCard)
                .isNotNull();
        assertThat(creditCard.isDefault())
                .isTrue();
        assertThat(creditCard.getCardHolderId())
                .isEqualTo(cardHolderId);
    }

    @And("Methods needed to retrieve default credit card were called")
    public void methodsNeededToRetrieveDefaultCreditCardWereCalled() {
        verify(creditCardRepository).findAllByCardHolderIdAndRole(cardHolderId, Role.PASSENGER);
    }
}
