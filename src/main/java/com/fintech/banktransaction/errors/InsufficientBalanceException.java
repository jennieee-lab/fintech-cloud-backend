package com.fintech.banktransaction.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
        value = HttpStatus.PRECONDITION_FAILED,
        reason = "Account has insufficient balance to perform the transaction.")
public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException() {
        super("Account has insufficient balance to perform the transaction.");
    }
}
