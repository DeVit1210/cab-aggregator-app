package com.modsen.payment.utils;

import com.modsen.payment.constants.TestConstants;
import com.modsen.payment.dto.request.CreditCardRequest;
import com.modsen.payment.dto.request.CustomerRequest;
import com.modsen.payment.dto.request.DriverPayoutRequest;
import com.modsen.payment.dto.request.PaymentRequest;
import com.modsen.payment.dto.response.CreditCardResponse;
import com.modsen.payment.dto.response.DriverPayoutResponse;
import com.modsen.payment.enums.Role;
import com.modsen.payment.model.CreditCard;
import com.modsen.payment.model.DriverAccount;
import com.modsen.payment.model.DriverPayout;
import com.modsen.payment.model.Payment;
import com.modsen.payment.model.StripeCustomer;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;

@UtilityClass
public class TestUtils {
    public static CustomerRequest defaultCustomerRequest() {
        return CustomerRequest.builder()
                .passengerId(TestConstants.PASSENGER_ID)
                .build();
    }

    public static StripeCustomer defaultStripeCustomer() {
        return StripeCustomer.builder()
                .id(TestConstants.PASSENGER_ID)
                .stripeCustomerId(TestConstants.Stripe.CUSTOMER_ID)
                .build();
    }

    public static DriverAccount defaultDriverAccount() {
        return driverAccountWithAmount(BigDecimal.ZERO);
    }

    public static DriverAccount driverAccountWithAmount(BigDecimal currentAmount) {
        return DriverAccount.builder()
                .driverId(TestConstants.DRIVER_ID)
                .amount(currentAmount)
                .build();
    }

    public static CreditCard defaultCreditCard() {
        return creditCardWithRoleAndIsDefault(Role.PASSENGER, false);
    }

    public static CreditCard creditCardWithRoleAndIsDefault(Role role, boolean isDefault) {
        return CreditCard.builder()
                .id(TestConstants.CREDIT_CARD_ID)
                .number(TestConstants.CreditCard.NUMBER)
                .stripeId(TestConstants.Stripe.CREDIT_CARD_ID)
                .cardHolderId(TestConstants.CARD_HOLDER_ID)
                .role(role)
                .isDefault(isDefault)
                .build();
    }

    public static CreditCardRequest defaultCreditCardRequest() {
        return creditCardRequestWithRoleAndIsDefault(Role.PASSENGER, false);
    }

    public static CreditCardRequest creditCardRequestWithRoleAndIsDefault(Role role, boolean isDefault) {
        return CreditCardRequest.builder()
                .cardHolderId(TestConstants.CARD_HOLDER_ID)
                .number(TestConstants.CreditCard.NUMBER)
                .expireMonth(TestConstants.CreditCard.MONTH_EXP)
                .expireYear(TestConstants.CreditCard.YEAR_EXP)
                .cvc(TestConstants.CreditCard.CVC)
                .role(role.name())
                .isDefault(isDefault)
                .build();
    }

    public static DriverPayoutRequest driverPayoutRequestWithAmount(BigDecimal amountToWithdraw) {
        return DriverPayoutRequest.builder()
                .driverId(TestConstants.DRIVER_ID)
                .amount(amountToWithdraw)
                .creditCardId(TestConstants.CREDIT_CARD_ID)
                .build();
    }

    public static CreditCardResponse creditCardResponseForRole(Role role) {
        return creditCardResponseForRoleAndHolderId(role, TestConstants.CARD_HOLDER_ID);
    }

    public static CreditCardResponse creditCardResponseForRoleAndHolderId(Role role, Long invalidCardHolderId) {
        return CreditCardResponse.builder()
                .id(TestConstants.CREDIT_CARD_ID)
                .cardHolderId(invalidCardHolderId)
                .role(role)
                .number(TestConstants.CreditCard.NUMBER)
                .build();
    }

    public static DriverPayout driverPayout(BigDecimal amountToWithdraw) {
        return DriverPayout.builder()
                .withdrawAmount(amountToWithdraw)
                .creditCardId(TestConstants.CREDIT_CARD_ID)
                .build();
    }

    public static DriverPayoutResponse driverPayoutResponse(BigDecimal amountToWithdraw, BigDecimal leftoverAmount) {
        return DriverPayoutResponse.builder()
                .withdrawAmount(amountToWithdraw)
                .leftoverAmount(leftoverAmount)
                .creditCardId(TestConstants.CREDIT_CARD_ID)
                .build();
    }

    public static Payment defaultPayment() {
        return Payment.builder()
                .rideId(TestConstants.RIDE_ID)
                .creditCardId(TestConstants.CREDIT_CARD_ID)
                .passengerId(TestConstants.PASSENGER_ID)
                .build();
    }

    public static PaymentRequest defaultPaymentRequest() {
        return paymentRequestWithAmount(BigDecimal.ONE);
    }

    public static PaymentRequest paymentRequestWithAmount(BigDecimal amount) {
        return PaymentRequest.builder()
                .rideId(TestConstants.RIDE_ID)
                .amount(amount)
                .driverId(TestConstants.DRIVER_ID)
                .passengerId(TestConstants.PASSENGER_ID)
                .build();
    }
}
