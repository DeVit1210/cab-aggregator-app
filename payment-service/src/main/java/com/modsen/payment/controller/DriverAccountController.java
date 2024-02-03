package com.modsen.payment.controller;

import com.modsen.payment.constants.ControllerMappings;
import com.modsen.payment.dto.response.DriverAccountResponse;
import com.modsen.payment.service.DriverAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ControllerMappings.DRIVER_ACCOUNT_CONTROLLER)
@RequiredArgsConstructor
public class DriverAccountController {
    private final DriverAccountService driverAccountService;

    @PostMapping("/{driverId}")
    @ResponseStatus(HttpStatus.CREATED)
    public DriverAccountResponse createDriverAccount(@Valid @PathVariable Long driverId) {
        return driverAccountService.createDriverAccount(driverId);
    }

    @GetMapping("/{accountId}")
    public DriverAccountResponse findAccountById(@PathVariable Long accountId) {
        return driverAccountService.findAccountById(accountId);
    }
}
