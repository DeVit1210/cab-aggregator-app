package com.modsen.payment.integration;

import com.modsen.payment.constants.MessageTemplates;
import com.modsen.payment.constants.TestConstants;
import com.modsen.payment.dto.request.CreditCardRequest;
import com.modsen.payment.dto.response.CreditCardResponse;
import com.modsen.payment.enums.Role;
import com.modsen.payment.model.CreditCard;
import com.modsen.payment.model.StripeCustomer;
import com.modsen.payment.repository.CreditCardRepository;
import com.modsen.payment.service.StripeService;
import com.modsen.payment.utils.RestAssuredUtils;
import com.modsen.payment.utils.TestUtils;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = "classpath:insert-stripe-customers-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public class CreditCardControllerIntegrationTest extends BaseTestContainer {
    @Autowired
    private CreditCardRepository creditCardRepository;
    @MockBean
    private StripeService stripeService;

    static Stream<Arguments> findAllByIdAndRoleArgumentsProvider() {
        return Stream.of(
                Arguments.of(1L, Role.PASSENGER, 2),
                Arguments.of(1L, Role.DRIVER, 2),
                Arguments.of(2L, Role.PASSENGER, 1),
                Arguments.of(2L, Role.DRIVER, 0)
        );
    }

    @AfterEach
    void tearDown() {
        creditCardRepository.deleteAll();
    }

    @ParameterizedTest
    @MethodSource("findAllByIdAndRoleArgumentsProvider")
    @Sql("classpath:insert-credit-cards-data.sql")
    void findAllByIdAndRole_Success(Long cardHolderId, Role role, int expectedCreditCardQuantity) {
        RestAssuredUtils.findAllByIdAndRoleResponse(cardHolderId, role)
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body(TestConstants.FieldNames.QUANTITY_FIELD, equalTo(expectedCreditCardQuantity));
    }

    @Test
    @Sql("classpath:insert-credit-cards-data.sql")
    void findCreditCardById_CardExists_ShouldReturnCreditCard() {
        CreditCardResponse expectedResponse =
                TestUtils.creditCardResponseForRoleAndHolderId(Role.DRIVER, 1L);

        CreditCardResponse creditCardResponse = RestAssuredUtils.findCreditCardByIdResponse(expectedResponse.id())
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(CreditCardResponse.class);

        assertEquals(expectedResponse, creditCardResponse);
    }

    @Test
    void findCreditCardById_CardDoesNotExist_ShouldReturnApiExceptionInfo() {
        Long creditCardId = TestConstants.CREDIT_CARD_ID;
        String expectedExceptionMessage = String.format(
                MessageTemplates.ENTITY_NOT_FOUND_BY_ID.getValue(),
                CreditCard.class.getSimpleName(),
                creditCardId
        );
        HttpStatus expectedStatusCode = HttpStatus.NOT_FOUND;

        Response creditCardByIdResponse = RestAssuredUtils.findCreditCardByIdResponse(creditCardId);

        extractApiExceptionInfoAndAssert(creditCardByIdResponse, expectedStatusCode, expectedExceptionMessage);
    }

    @Test
    @Sql("classpath:insert-credit-cards-data.sql")
    void setDefaultCreditCard_ValidCardId_ShouldReturnNewDefaultCard() {
        Long oldDefaultCardId = 4L;
        Long newDefaultCardId = 3L;

        Mockito.doNothing()
                .when(stripeService)
                .setDefaultCreditCard(anyString(), anyString());


        RestAssuredUtils.changeDefaultCardResponse(newDefaultCardId)
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body(TestConstants.FieldNames.ID_FIELD, equalTo(newDefaultCardId.intValue()));

        Optional<CreditCard> oldDefaultCreditCard = creditCardRepository.findById(oldDefaultCardId);
        Optional<CreditCard> newDefaultCreditCard = creditCardRepository.findById(newDefaultCardId);

        Mockito.verify(stripeService).setDefaultCreditCard(anyString(), anyString());
        assertThat(oldDefaultCreditCard)
                .isPresent()
                .get()
                .extracting(CreditCard::isDefault)
                .isEqualTo(false);
        assertThat(newDefaultCreditCard)
                .isPresent()
                .get()
                .extracting(CreditCard::isDefault)
                .isEqualTo(true);
    }

    @Test
    void setDefaultCreditCard_InvalidCardId_ShouldReturnApiExceptionInfo() {
        Long creditCardId = TestConstants.CREDIT_CARD_ID;
        String expectedExceptionMessage = String.format(
                MessageTemplates.ENTITY_NOT_FOUND_BY_ID.getValue(),
                CreditCard.class.getSimpleName(),
                creditCardId
        );
        HttpStatus expectedStatusCode = HttpStatus.NOT_FOUND;

        Response changeDefaultCardResponse = RestAssuredUtils.changeDefaultCardResponse(creditCardId);

        extractApiExceptionInfoAndAssert(changeDefaultCardResponse, expectedStatusCode, expectedExceptionMessage);
    }

    @Test
    @Sql("classpath:insert-credit-cards-data.sql")
    void setDefaultCreditCard_StripeCustomerDoesNotExist_ShouldReturnApiExceptionInfo() {
        Long notDefaultCreditCard = 6L;
        String expectedExceptionMessage = String.format(
                MessageTemplates.ENTITY_NOT_FOUND_BY_ID.getValue(),
                StripeCustomer.class.getSimpleName(),
                notDefaultCreditCard
        );
        HttpStatus expectedHttpStatus = HttpStatus.NOT_FOUND;

        Response changeDefaultCardResponse = RestAssuredUtils.changeDefaultCardResponse(notDefaultCreditCard);

        extractApiExceptionInfoAndAssert(changeDefaultCardResponse, expectedHttpStatus, expectedExceptionMessage);
    }

    @Test
    void createCreditCard_ValidCreditCardRequest_ShouldReturnCreatedCard() {
        CreditCardRequest creditCardRequest = TestUtils.creditCardRequestWithRoleAndIsDefault(Role.PASSENGER, true);
        CreditCard expectedCreditCard = TestUtils.creditCardWithRoleAndIsDefault(Role.PASSENGER, true);

        Mockito.when(stripeService.createTokenForCreditCard(any()))
                .thenReturn(TestConstants.Stripe.CREDIT_CARD_ID);
        Mockito.doNothing()
                .when(stripeService)
                .setDefaultCreditCard(anyString(), anyString());

        CreditCardResponse creditCardResponse = RestAssuredUtils.createCreditCardResponse(creditCardRequest)
                .then()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(CreditCardResponse.class);

        Optional<CreditCard> createdCreditCard = creditCardRepository.findById(creditCardResponse.id());

        assertTrue(createdCreditCard.isPresent());
        assertThat(createdCreditCard.get())
                .usingRecursiveComparison()
                .ignoringFields(TestConstants.FieldNames.ID_FIELD)
                .isEqualTo(expectedCreditCard);
    }

    @Test
    @Sql("classpath:insert-credit-cards-data.sql")
    void createCreditCard_CardAlreadyExists_ShouldReturnApiExceptionInfo() {
        CreditCardRequest creditCardRequest = TestUtils.defaultCreditCardRequest();
        String expectedExceptionMessage = String.format(
                MessageTemplates.CREDIT_CARD_ALREADY_EXISTS.getValue(),
                creditCardRequest.getNumber(),
                creditCardRequest.getRole(),
                creditCardRequest.getCardHolderId()
        );
        HttpStatus expectedHttpStatus = HttpStatus.CONFLICT;

        Response createCreditCardResponse = RestAssuredUtils.createCreditCardResponse(creditCardRequest);

        extractApiExceptionInfoAndAssert(createCreditCardResponse, expectedHttpStatus, expectedExceptionMessage);
    }

    @Test
    void createCreditCard_CardShouldBeDefault_ShouldReturnApiExceptionInfo() {
        CreditCardRequest creditCardRequest = TestUtils.defaultCreditCardRequest();
        String expectedExceptionMessage = String.format(
                MessageTemplates.DEFAULT_CREDIT_CARD.getValue(),
                creditCardRequest.getNumber(),
                creditCardRequest.getRole(),
                creditCardRequest.getCardHolderId()
        );
        HttpStatus expectedHttpStatus = HttpStatus.BAD_REQUEST;

        Response createCreditCardResponse = RestAssuredUtils.createCreditCardResponse(creditCardRequest);

        extractApiExceptionInfoAndAssert(createCreditCardResponse, expectedHttpStatus, expectedExceptionMessage);
    }
}
