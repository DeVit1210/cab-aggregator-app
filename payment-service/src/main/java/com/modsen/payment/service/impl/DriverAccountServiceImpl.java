package com.modsen.payment.service.impl;

import com.modsen.payment.dto.response.DriverAccountResponse;
import com.modsen.payment.exception.AccountNotFoundForDriverIdException;
import com.modsen.payment.exception.DriverAccountAlreadyExistsException;
import com.modsen.payment.exception.IncufficientAccountBalanceException;
import com.modsen.payment.exception.PaymentEntityNotFoundException;
import com.modsen.payment.mapper.DriverAccountMapper;
import com.modsen.payment.model.DriverAccount;
import com.modsen.payment.repository.DriverAccountRepository;
import com.modsen.payment.service.DriverAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DriverAccountServiceImpl implements DriverAccountService {
    private final DriverAccountRepository driverAccountRepository;
    private final DriverAccountMapper driverAccountMapper;

    @Override
    @Transactional
    public DriverAccountResponse createDriverAccount(Long driverId) {
        if (driverAccountRepository.existsById(driverId)) {
            throw new DriverAccountAlreadyExistsException(driverId);
        }
        DriverAccount driverAccount = DriverAccount.builder()
                .driverId(driverId)
                .amount(BigDecimal.ZERO)
                .build();
        DriverAccount savedDriverAccount = driverAccountRepository.save(driverAccount);
        return driverAccountMapper.toDriverAccountResponse(savedDriverAccount);
    }

    @Override
    public DriverAccountResponse findAccountById(Long driverAccountId) {
        return driverAccountRepository.findById(driverAccountId)
                .map(driverAccountMapper::toDriverAccountResponse)
                .orElseThrow(() -> new PaymentEntityNotFoundException(driverAccountId, DriverAccount.class));
    }

    @Override
    public DriverAccount findAccountByDriverId(Long driverId) {
        return driverAccountRepository.findById(driverId)
                .orElseThrow(() -> new AccountNotFoundForDriverIdException(driverId));
    }

    @Override
    public void replenishAccount(Long driverId, BigDecimal amount) {
        DriverAccount driverAccount = driverAccountRepository.findById(driverId)
                .orElseThrow(() -> new AccountNotFoundForDriverIdException(driverId));
        BigDecimal currentAmount = driverAccount.getAmount();
        driverAccount.setAmount(currentAmount.add(amount));
        driverAccountRepository.save(driverAccount);
    }

    @Override
    public DriverAccount withdraw(Long driverId, BigDecimal amountToWithdraw) {
        DriverAccount driverAccount = findAccountByDriverId(driverId);
        BigDecimal currentBalance = driverAccount.getAmount();
        if (amountToWithdraw.compareTo(currentBalance) >= 0) {
            throw new IncufficientAccountBalanceException(amountToWithdraw);
        }
        driverAccount.setAmount(currentBalance.subtract(amountToWithdraw));

        return driverAccountRepository.save(driverAccount);
    }
}
