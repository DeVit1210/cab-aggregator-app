package com.modsen.payment.service.impl;

import com.modsen.payment.constants.MessageTemplates;
import com.modsen.payment.constants.TestConstants;
import com.modsen.payment.dto.request.CustomerRequest;
import com.modsen.payment.dto.response.StripeCustomerResponse;
import com.modsen.payment.exception.CustomStripeException;
import com.modsen.payment.exception.PaymentEntityNotFoundException;
import com.modsen.payment.exception.StripeCustomerAlreadyExistsException;
import com.modsen.payment.mapper.StripeCustomerMapperImpl;
import com.modsen.payment.model.StripeCustomer;
import com.modsen.payment.repository.StripeCustomerRepository;
import com.modsen.payment.service.StripeService;
import com.modsen.payment.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StripeCustomerServiceImplTest {
    @Mock
    private StripeCustomerRepository stripeCustomerRepository;
    @Mock
    private StripeCustomerMapperImpl stripeCustomerMapper;
    @Mock
    private StripeService stripeService;
    @InjectMocks
    private StripeCustomerServiceImpl stripeCustomerService;

    @Test
    void createStripeCustomer_ValidCustomerRequest_ShouldReturnCreatedCustomer() {
        String stripeCustomerId = TestConstants.Stripe.CUSTOMER_ID;
        CustomerRequest customerRequest = TestUtils.defaultCustomerRequest();
        StripeCustomer stripeCustomer = TestUtils.defaultStripeCustomer();

        when(stripeService.createStripeCustomer(any(CustomerRequest.class)))
                .thenReturn(stripeCustomerId);
        when(stripeCustomerRepository.save(any(StripeCustomer.class)))
                .thenReturn(stripeCustomer);
        when(stripeCustomerMapper.toStripeCustomerResponse(any(StripeCustomer.class)))
                .thenCallRealMethod();

        StripeCustomerResponse createdCustomer = stripeCustomerService.createStripeCustomer(customerRequest);

        assertNotNull(createdCustomer);
        assertEquals(TestConstants.PASSENGER_ID, createdCustomer.id());
        assertEquals(stripeCustomerId, createdCustomer.stripeCustomerId());
        verify(stripeService).createStripeCustomer(customerRequest);
        verify(stripeCustomerRepository).save(stripeCustomer);
    }

    @Test
    void createStripeCustomer_CannotCreateStripeCustomer_ThrowCustomStripeException() {
        CustomerRequest customerRequest = TestUtils.defaultCustomerRequest();

        when(stripeService.createStripeCustomer(any(CustomerRequest.class)))
                .thenThrow(CustomStripeException.class);

        assertThrowsExactly(CustomStripeException.class, () -> stripeCustomerService.createStripeCustomer(customerRequest));
        verify(stripeService).createStripeCustomer(customerRequest);
        verify(stripeCustomerRepository, never()).save(any(StripeCustomer.class));
    }

    @Test
    void createStripeCustomer_DuplicateCustomerId_ThrowCustomerAlreadyExistsException() {
        CustomerRequest customerRequest = TestUtils.defaultCustomerRequest();
        String exceptionMessage =
                String.format(MessageTemplates.STRIPE_CUSTOMER_ALREADY_EXISTS.getValue(), TestConstants.PASSENGER_ID);

        when(stripeCustomerRepository.existsById(anyLong()))
                .thenReturn(true);

        assertThatThrownBy(() -> stripeCustomerService.createStripeCustomer(customerRequest))
                .isInstanceOf(StripeCustomerAlreadyExistsException.class)
                .hasMessage(exceptionMessage);
        verify(stripeCustomerRepository).existsById(customerRequest.getPassengerId());
        verify(stripeService, never()).createStripeCustomer(any(CustomerRequest.class));
        verify(stripeCustomerRepository, never()).save(any(StripeCustomer.class));
    }

    @Test
    void getCustomerId_ValidPassengerId_ShouldReturnStripeCustomerId() {
        Long passengerId = TestConstants.PASSENGER_ID;
        StripeCustomer stripeCustomer = TestUtils.defaultStripeCustomer();

        when(stripeCustomerRepository.findById(anyLong()))
                .thenReturn(Optional.of(stripeCustomer));

        String actualStripeCustomerId = stripeCustomerService.getCustomerId(passengerId);

        assertEquals(actualStripeCustomerId, stripeCustomer.getStripeCustomerId());
        verify(stripeCustomerRepository).findById(passengerId);
    }

    @Test
    void getCustomerId_CustomerDoesNotExist_ThrowPaymentEntityNotFoundException() {
        Long passengerId = TestConstants.PASSENGER_ID;
        String exceptionMessage = String.format(
                MessageTemplates.ENTITY_NOT_FOUND_BY_ID.getValue(),
                StripeCustomer.class.getSimpleName(),
                passengerId
        );

        when(stripeCustomerRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> stripeCustomerService.getCustomerId(passengerId))
                .isInstanceOf(PaymentEntityNotFoundException.class)
                .hasMessage(exceptionMessage);
    }

    @Test
    void findStripeCustomerById_CustomerExists_ShouldReturnStripeCustomer() {
        Long passengerId = TestConstants.PASSENGER_ID;
        StripeCustomer stripeCustomer = TestUtils.defaultStripeCustomer();

        when(stripeCustomerRepository.findById(anyLong()))
                .thenReturn(Optional.of(stripeCustomer));
        when(stripeCustomerMapper.toStripeCustomerResponse(any(StripeCustomer.class)))
                .thenCallRealMethod();

        StripeCustomerResponse actualStripeCustomer = stripeCustomerService.findStripeCustomerById(passengerId);

        assertNotNull(actualStripeCustomer);
        verify(stripeCustomerRepository).findById(passengerId);
        verify(stripeCustomerMapper).toStripeCustomerResponse(stripeCustomer);
    }

    @Test
    void findStripeCustomerById_CustomerDoesNotExist_ThrowPaymentEntityNotFound() {
        Long passengerId = TestConstants.PASSENGER_ID;
        String exceptionMessage = String.format(
                MessageTemplates.ENTITY_NOT_FOUND_BY_ID.getValue(),
                StripeCustomer.class.getSimpleName(),
                passengerId
        );

        when(stripeCustomerRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> stripeCustomerService.findStripeCustomerById(passengerId))
                .isInstanceOf(PaymentEntityNotFoundException.class)
                .hasMessage(exceptionMessage);
    }
}