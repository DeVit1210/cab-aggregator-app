package com.modsen.payment.service.impl;

import com.modsen.payment.constants.MessageTemplates;
import com.modsen.payment.constants.TestConstants;
import com.modsen.payment.dto.request.DriverPayoutRequest;
import com.modsen.payment.dto.response.CreditCardResponse;
import com.modsen.payment.dto.response.DriverPayoutListResponse;
import com.modsen.payment.dto.response.DriverPayoutResponse;
import com.modsen.payment.enums.Role;
import com.modsen.payment.exception.InvalidCreditCardHolderException;
import com.modsen.payment.exception.PaymentEntityNotFoundException;
import com.modsen.payment.mapper.DriverPayoutMapperImpl;
import com.modsen.payment.model.DriverAccount;
import com.modsen.payment.model.DriverPayout;
import com.modsen.payment.repository.DriverPayoutRepository;
import com.modsen.payment.service.CreditCardService;
import com.modsen.payment.service.DriverAccountService;
import com.modsen.payment.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DriverPayoutServiceImplTest {
    @Mock
    private DriverPayoutRepository driverPayoutRepository;
    @Mock
    private DriverPayoutMapperImpl driverPayoutMapper;
    @Mock
    private DriverAccountService driverAccountService;
    @Mock
    private CreditCardService creditCardService;
    @InjectMocks
    private DriverPayoutServiceImpl driverPayoutService;

    @Test
    void createDriverPayout_ValidPayoutRequest_ShouldWithdrawAndReturnCreatedPayout() {
        BigDecimal amountToWithdraw = BigDecimal.ONE;
        BigDecimal leftoverAmount = BigDecimal.TEN;
        DriverPayoutRequest payoutRequest = TestUtils.driverPayoutRequestWithAmount(amountToWithdraw);
        CreditCardResponse creditCardResponse = TestUtils.creditCardResponseForRole(Role.DRIVER);
        DriverAccount updatedAccount = TestUtils.driverAccountWithAmount(leftoverAmount);
        DriverPayoutResponse driverPayoutResponse = TestUtils.driverPayoutResponse(amountToWithdraw, leftoverAmount);

        when(creditCardService.findCardById(anyLong()))
                .thenReturn(creditCardResponse);
        when(driverAccountService.withdraw(anyLong(), any(BigDecimal.class)))
                .thenReturn(updatedAccount);
        when(driverPayoutMapper.toDriverPayoutResponse(any(DriverPayout.class), any(BigDecimal.class)))
                .thenReturn(driverPayoutResponse);

        DriverPayoutResponse actualDriverPayout = driverPayoutService.createPayout(payoutRequest);

        assertEquals(amountToWithdraw, actualDriverPayout.withdrawAmount());
        assertEquals(leftoverAmount, actualDriverPayout.leftoverAmount());
        verify(creditCardService).findCardById(payoutRequest.getCreditCardId());
        verify(driverAccountService).withdraw(payoutRequest.getDriverId(), payoutRequest.getAmount());
        verify(driverPayoutRepository).save(any());
        verify(driverPayoutMapper).toDriverPayoutResponse(any(), any());
    }

    @Test
    void createDriverPayout_CreditCardBelongToPassenger_ThrowInvalidCreditCardHolderException() {
        DriverPayoutRequest driverPayoutRequest = TestUtils.driverPayoutRequestWithAmount(BigDecimal.ONE);
        CreditCardResponse creditCardResponse = TestUtils.creditCardResponseForRole(Role.PASSENGER);
        String exceptionMessage = String.format(
                MessageTemplates.CREDIT_CARD_INVALID_HOLDER.getValue(),
                creditCardResponse.id(),
                Role.DRIVER.name().toLowerCase(),
                driverPayoutRequest.getDriverId()
        );

        when(creditCardService.findCardById(anyLong()))
                .thenReturn(creditCardResponse);

        assertThatThrownBy(() -> driverPayoutService.createPayout(driverPayoutRequest))
                .isInstanceOf(InvalidCreditCardHolderException.class)
                .hasMessage(exceptionMessage);
        verify(creditCardService).findCardById(driverPayoutRequest.getCreditCardId());
        verify(driverAccountService, never()).withdraw(anyLong(), any());
        verify(driverPayoutRepository, never()).save(any());
    }

    @Test
    void createDriverPayout_CreditCardBelongToAnotherDriver_ThrowInvalidCreditCardHolderException() {
        Long invalidCardHolderId = 100L;
        DriverPayoutRequest driverPayoutRequest = TestUtils.driverPayoutRequestWithAmount(BigDecimal.ONE);
        CreditCardResponse creditCardResponse =
                TestUtils.creditCardResponseForRoleAndHolderId(Role.DRIVER, invalidCardHolderId);
        String exceptionMessage = String.format(
                MessageTemplates.CREDIT_CARD_INVALID_HOLDER.getValue(),
                creditCardResponse.id(),
                Role.DRIVER.name().toLowerCase(),
                driverPayoutRequest.getDriverId()
        );

        when(creditCardService.findCardById(anyLong()))
                .thenReturn(creditCardResponse);

        assertThatThrownBy(() -> driverPayoutService.createPayout(driverPayoutRequest))
                .isInstanceOf(InvalidCreditCardHolderException.class)
                .hasMessage(exceptionMessage);
        verify(creditCardService).findCardById(driverPayoutRequest.getCreditCardId());
        verify(driverAccountService, never()).withdraw(anyLong(), any());
        verify(driverPayoutRepository, never()).save(any());
    }

    @Test
    void getAllPayoutsForDriver_AtLeastOnePayoutExists_ReturnDriverPayouts() {
        Long driverId = TestConstants.DRIVER_ID;
        DriverAccount driverAccount = TestUtils.defaultDriverAccount();
        List<DriverPayout> driverPayoutList = Collections.nCopies(3, TestUtils.driverPayout(BigDecimal.ONE));

        when(driverAccountService.findAccountByDriverId(anyLong()))
                .thenReturn(driverAccount);
        when(driverPayoutRepository.findAllByAccount(any(DriverAccount.class)))
                .thenReturn(driverPayoutList);
        when(driverPayoutMapper.toDriverPayoutListResponse(anyList()))
                .thenCallRealMethod();

        DriverPayoutListResponse actualDriverPayouts = driverPayoutService.getAllPayoutsForDriver(driverId);

        assertEquals(driverPayoutList.size(), actualDriverPayouts.size());
        verify(driverAccountService).findAccountByDriverId(driverId);
        verify(driverPayoutRepository).findAllByAccount(driverAccount);
        verify(driverPayoutMapper).toDriverPayoutListResponse(driverPayoutList);
    }

    @Test
    void getAllPayoutsForDriver_NoPayoutsExist_ReturnEmptyList() {
        Long driverId = TestConstants.DRIVER_ID;
        DriverAccount driverAccount = TestUtils.defaultDriverAccount();

        when(driverAccountService.findAccountByDriverId(anyLong()))
                .thenReturn(driverAccount);
        when(driverPayoutRepository.findAllByAccount(any(DriverAccount.class)))
                .thenReturn(Collections.emptyList());

        DriverPayoutListResponse actualDriverPayouts = driverPayoutService.getAllPayoutsForDriver(driverId);

        assertTrue(actualDriverPayouts.payouts().isEmpty());
        verify(driverAccountService).findAccountByDriverId(driverId);
        verify(driverPayoutRepository).findAllByAccount(driverAccount);
    }

    @Test
    void getAllPayoutsForDriver_InvalidDriverId_ThrowPaymentEntityNotFoundException() {
        Long driverId = TestConstants.DRIVER_ID;
        when(driverAccountService.findAccountByDriverId(anyLong()))
                .thenThrow(PaymentEntityNotFoundException.class);

        assertThrowsExactly(PaymentEntityNotFoundException.class, () -> driverPayoutService.getAllPayoutsForDriver(driverId));
    }
}