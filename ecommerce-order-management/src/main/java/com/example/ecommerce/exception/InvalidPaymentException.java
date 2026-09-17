package com.example.ecommerce.exception;

public class InvalidPaymentException
        extends RuntimeException {

    public InvalidPaymentException(String message) {
        super(message);
    }
}