package com.modsen.payment.dto.response;

import java.math.BigDecimal;

public class DriverPayoutResponse {
    private Long id;
    private Long driverId;
    private BigDecimal amount;
    private String cardNumber;
}
