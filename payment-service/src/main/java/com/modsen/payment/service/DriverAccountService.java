package com.modsen.payment.service;


import com.modsen.payment.dto.response.DriverAccountResponse;
import com.modsen.payment.model.DriverAccount;

import java.math.BigDecimal;

public interface DriverAccountService {
    DriverAccountResponse createDriverAccount(Long driverId);

    DriverAccount findAccountById(Long driverAccountId);

    DriverAccount findAccountByDriverId(Long driverId);

    void replenishAccount(Long driverId, BigDecimal amount);
}
