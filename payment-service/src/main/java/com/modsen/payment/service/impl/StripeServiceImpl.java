package com.modsen.payment.service.impl;

import com.modsen.payment.advice.PublishableKey;
import com.modsen.payment.dto.request.CreditCardRequest;
import com.modsen.payment.dto.request.CustomerRequest;
import com.modsen.payment.service.StripeService;
import com.stripe.Stripe;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.model.Token;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerUpdateParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentMethodAttachParams;
import com.stripe.param.PaymentMethodCreateParams;
import com.stripe.param.TokenCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class StripeServiceImpl implements StripeService {
    @Value("${stripe.key.secret}")
    private String SK_KEY;

    @PostConstruct
    public void setUp() {
        Stripe.apiKey = SK_KEY;
    }

    @Override
    @SneakyThrows
    @PublishableKey
    public String createTokenForCreditCard(CreditCardRequest request) {
        TokenCreateParams params = TokenCreateParams.builder()
                .setCard(TokenCreateParams.Card.builder()
                        .setNumber(request.getNumber())
                        .setExpMonth(String.valueOf(request.getExpireMonth()))
                        .setExpYear(String.valueOf(request.getExpireYear()))
                        .setCvc(String.valueOf(request.getCvc()))
                        .build()
                )
                .build();
        Token token = Token.create(params);
        PaymentMethod paymentMethod = createPaymentMethod(token.getId());

        return paymentMethod.getId();
    }

    @Override
    @SneakyThrows
    public String createStripeCustomer(CustomerRequest request) {
        CustomerCreateParams params = CustomerCreateParams.builder()
                .setName(request.getName())
                .setEmail(request.getEmail())
                .build();
        Customer customer = Customer.create(params);

        return customer.getId();
    }

    @Override
    @SneakyThrows
    public void setDefaultCreditCard(String stripeCustomerId, String creditCardToken) {
//        PaymentMethod paymentMethod = createPaymentMethod(creditCardToken);
        PaymentMethod paymentMethod = PaymentMethod.retrieve(creditCardToken);
        PaymentMethod attachedPaymentMethod = attachPaymentMethodToCustomer(paymentMethod, stripeCustomerId);
        CustomerUpdateParams params = CustomerUpdateParams.builder()
                .setInvoiceSettings(CustomerUpdateParams.InvoiceSettings.builder()
                        .setDefaultPaymentMethod(attachedPaymentMethod.getId())
                        .build()
                )
                .build();
        Customer customer = Customer.retrieve(stripeCustomerId);
        customer.update(params);
    }

    @Override
    @SneakyThrows
    public String createPayment(String stripeCustomerId, BigDecimal amount) {
        Customer customer = Customer.retrieve(stripeCustomerId);
        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                        .setAmount(amount.longValue())
                        .setCurrency("usd")
                        .setCustomer(customer.getId())
                        .setPaymentMethod(customer.getInvoiceSettings().getDefaultPaymentMethod())
                        .setConfirm(true)
                        .setAutomaticPaymentMethods(
                                PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                        .putExtraParam("allow_redirects", "never")
                                        .setEnabled(true)
                                        .build())
                        .build();
        PaymentIntent paymentIntent = PaymentIntent.create(params);

        return paymentIntent.getId();
    }

    @SneakyThrows
    private PaymentMethod createPaymentMethod(String creditCardToken) {
        PaymentMethodCreateParams paymentMethodCreateParams = PaymentMethodCreateParams.builder()
                .setType(PaymentMethodCreateParams.Type.CARD)
                .setCard(PaymentMethodCreateParams.Token.builder().setToken(creditCardToken).build())
                .build();

        return PaymentMethod.create(paymentMethodCreateParams);
    }

    @SneakyThrows
    private PaymentMethod attachPaymentMethodToCustomer(PaymentMethod paymentMethod, String customerId) {
        PaymentMethodAttachParams params = PaymentMethodAttachParams.builder()
                .setCustomer(customerId)
                .build();

        return paymentMethod.attach(params);
    }
}
