package com.modsen.payment.exception;

import com.modsen.payment.constants.MessageTemplates;

import java.math.BigDecimal;

public class IncufficientAccountBalanceException extends RuntimeException {
    public IncufficientAccountBalanceException(BigDecimal amountToWithdraw) {
        super(String.format(MessageTemplates.INCUFFICIENT_ACCOUNT_BALANCE.getValue(), amountToWithdraw));
    }
}
