package com.modsen.payment.service;


import com.modsen.payment.dto.response.DriverAccountResponse;
import com.modsen.payment.model.DriverAccount;

import java.math.BigDecimal;

public interface DriverAccountService {
    DriverAccountResponse createDriverAccount(Long driverId);

    DriverAccountResponse findAccountById(Long driverAccountId);

    DriverAccount findAccountByDriverId(Long driverId);

    void replenishAccount(Long driverId, BigDecimal amount);

    DriverAccount withdraw(Long driverId, BigDecimal amount);
}
