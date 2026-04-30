package com.fintech.banktransaction.errors;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resourceName, Long resourceId) {
        super(resourceName + " with id " + resourceId + " was not found.");
    }
}
