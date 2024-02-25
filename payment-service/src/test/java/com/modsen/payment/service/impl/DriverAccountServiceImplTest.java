package com.modsen.payment.service.impl;

import com.modsen.payment.constants.MessageTemplates;
import com.modsen.payment.constants.TestConstants;
import com.modsen.payment.dto.response.DriverAccountResponse;
import com.modsen.payment.exception.DriverAccountAlreadyExistsException;
import com.modsen.payment.exception.IncufficientAccountBalanceException;
import com.modsen.payment.exception.PaymentEntityNotFoundException;
import com.modsen.payment.mapper.DriverAccountMapperImpl;
import com.modsen.payment.model.DriverAccount;
import com.modsen.payment.repository.DriverAccountRepository;
import com.modsen.payment.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DriverAccountServiceImplTest {
    @Mock
    private DriverAccountRepository driverAccountRepository;
    @Mock
    private DriverAccountMapperImpl driverAccountMapper;
    @InjectMocks
    private DriverAccountServiceImpl driverAccountService;

    @Test
    void createDriverAccount_AccountDoesNotExistForDriver_ShouldReturnCreatedAccount() {
        Long driverId = TestConstants.DRIVER_ID;
        DriverAccount driverAccount = TestUtils.defaultDriverAccount();

        when(driverAccountRepository.save(any(DriverAccount.class)))
                .thenReturn(driverAccount);
        when(driverAccountMapper.toDriverAccountResponse(any(DriverAccount.class)))
                .thenCallRealMethod();

        DriverAccountResponse createdDriverAccount = driverAccountService.createDriverAccount(driverId);

        assertNotNull(createdDriverAccount);
        assertEquals(driverId, createdDriverAccount.driverId());
        verify(driverAccountRepository).save(driverAccount);
        verify(driverAccountMapper).toDriverAccountResponse(driverAccount);
    }

    @Test
    void createDriverAccount_AccountExistsForDriver_ThrowDriverAccountAlreadyExistsException() {
        Long driverId = TestConstants.DRIVER_ID;
        String exceptionMessage = String.format(MessageTemplates.DRIVER_ACCOUNT_ALREADY_EXISTS.getValue(), driverId);

        when(driverAccountRepository.existsById(anyLong()))
                .thenReturn(true);

        assertThatThrownBy(() -> driverAccountService.createDriverAccount(driverId))
                .isInstanceOf(DriverAccountAlreadyExistsException.class)
                .hasMessage(exceptionMessage);
        verify(driverAccountRepository).existsById(driverId);
        verify(driverAccountRepository, never()).save(any(DriverAccount.class));
    }

    @Test
    void findAccountById_DriverAccountExists_ShouldReturnAccount() {
        Long driverAccountId = TestConstants.DRIVER_ID;
        DriverAccount driverAccount = TestUtils.defaultDriverAccount();

        when(driverAccountRepository.findById(anyLong()))
                .thenReturn(Optional.of(driverAccount));
        when(driverAccountMapper.toDriverAccountResponse(any(DriverAccount.class)))
                .thenCallRealMethod();

        DriverAccountResponse actualDriverAccount = driverAccountService.findAccountById(driverAccountId);

        assertNotNull(actualDriverAccount);
        verify(driverAccountRepository).findById(driverAccountId);
        verify(driverAccountMapper).toDriverAccountResponse(driverAccount);
    }

    @Test
    void findAccountById_DriverAccountDoesNotExist_ThrowPaymentEntityNotFoundException() {
        Long driverAccountId = TestConstants.DRIVER_ID;
        String exceptionMessage = String.format(
                MessageTemplates.ENTITY_NOT_FOUND_BY_ID.getValue(),
                DriverAccount.class.getSimpleName(),
                driverAccountId
        );

        when(driverAccountRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> driverAccountService.findAccountById(driverAccountId))
                .isInstanceOf(PaymentEntityNotFoundException.class)
                .hasMessage(exceptionMessage);
        verify(driverAccountRepository).findById(driverAccountId);
    }

    @Test
    void replenishAccount_ValidAccountIdAndAmount_ShouldUpdateAccountBalance() {
        Long driverAccountId = TestConstants.DRIVER_ID;
        DriverAccount driverAccount = TestUtils.defaultDriverAccount();
        BigDecimal amountToReplenish = BigDecimal.TEN;
        BigDecimal expectedNewAccountBalance = driverAccount.getAmount().add(amountToReplenish);

        when(driverAccountRepository.findById(anyLong()))
                .thenReturn(Optional.of(driverAccount));
        when(driverAccountRepository.save(any(DriverAccount.class)))
                .thenReturn(driverAccount);

        driverAccountService.replenishAccount(driverAccountId, amountToReplenish);

        assertEquals(expectedNewAccountBalance, driverAccount.getAmount());
        verify(driverAccountRepository).findById(driverAccountId);
        verify(driverAccountRepository).save(driverAccount);
    }

    @Test
    void replenishAccount_InvalidAccountId_ThrowPaymentEntityNotFoundException() {
        Long driverAccountId = TestConstants.DRIVER_ID;
        String exceptionMessage = String.format(
                MessageTemplates.ENTITY_NOT_FOUND_BY_ID.getValue(),
                DriverAccount.class.getSimpleName(),
                driverAccountId
        );

        when(driverAccountRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> driverAccountService.replenishAccount(driverAccountId, BigDecimal.TEN))
                .isInstanceOf(PaymentEntityNotFoundException.class)
                .hasMessage(exceptionMessage);
    }

    @Test
    void withdraw_InvalidAmount_ThrowInsufficientAccountBalanceException() {
        Long driverAccountId = TestConstants.DRIVER_ID;
        DriverAccount driverAccount = TestUtils.driverAccountWithAmount(BigDecimal.valueOf(10.0));
        BigDecimal amountToWithdraw = BigDecimal.valueOf(20.0);
        String exceptionMessage = String.format(MessageTemplates.INCUFFICIENT_ACCOUNT_BALANCE.getValue(), amountToWithdraw);

        when(driverAccountRepository.findById(anyLong()))
                .thenReturn(Optional.of(driverAccount));
        assertThatThrownBy(() -> driverAccountService.withdraw(driverAccountId, amountToWithdraw))
                .isInstanceOf(IncufficientAccountBalanceException.class)
                .hasMessage(exceptionMessage);
        verify(driverAccountRepository, never()).save(any(DriverAccount.class));
    }
}

