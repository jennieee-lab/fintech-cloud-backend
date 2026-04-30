package com.fintech.banktransaction.errors;

public record FieldValidationError(String field, String message) {
}
