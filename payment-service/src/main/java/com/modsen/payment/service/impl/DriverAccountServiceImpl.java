package com.modsen.payment.service.impl;

import com.modsen.payment.dto.response.DriverAccountResponse;
import com.modsen.payment.exception.AccountNotFoundForDriverIdException;
import com.modsen.payment.exception.DriverAccountAlreadyExists;
import com.modsen.payment.exception.PaymentEntityNotFoundException;
import com.modsen.payment.mapper.DriverAccountMapper;
import com.modsen.payment.model.DriverAccount;
import com.modsen.payment.repository.DriverAccountRepository;
import com.modsen.payment.service.DriverAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DriverAccountServiceImpl implements DriverAccountService {
    private final DriverAccountRepository driverAccountRepository;
    private final DriverAccountMapper driverAccountMapper;

    @Override
    public DriverAccountResponse createDriverAccount(Long driverId) {
        if (driverAccountRepository.existsById(driverId)) {
            throw new DriverAccountAlreadyExists(driverId);
        }
        DriverAccount driverAccount = DriverAccount.builder()
                .driverId(driverId)
                .build();
        DriverAccount savedDriverAccount = driverAccountRepository.save(driverAccount);
        return driverAccountMapper.toDriverAccountResponse(savedDriverAccount);
    }

    @Override
    public DriverAccount findAccountById(Long driverAccountId) {
        return driverAccountRepository.findById(driverAccountId)
                .orElseThrow(() -> new PaymentEntityNotFoundException(driverAccountId, DriverAccount.class));
    }

    @Override
    public DriverAccount findAccountByDriverId(Long driverId) {
        return driverAccountRepository.findByDriverId(driverId)
                .orElseThrow(() -> new AccountNotFoundForDriverIdException(driverId));
    }

    @Override
    public void replenishAccount(Long driverId, BigDecimal amount) {
        DriverAccount driverAccount = driverAccountRepository.findByDriverId(driverId)
                .orElseThrow(() -> new AccountNotFoundForDriverIdException(driverId));
        BigDecimal currentAmount = driverAccount.getAmount();
        driverAccount.setAmount(currentAmount.add(amount));
        driverAccountRepository.save(driverAccount);
    }
}
