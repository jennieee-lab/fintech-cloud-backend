package com.fintech.banktransaction.errors;

public class InvalidAccountTypeException extends RuntimeException {
    public InvalidAccountTypeException(String accountType) {
        super("Unsupported account type: " + accountType + ". Allowed values are personal or business.");
    }
}
