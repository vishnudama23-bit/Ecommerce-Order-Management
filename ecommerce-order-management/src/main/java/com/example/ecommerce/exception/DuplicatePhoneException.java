package com.example.ecommerce.exception;

public class DuplicatePhoneException extends RuntimeException {

    public DuplicatePhoneException(String message) {
        super(message);
    }
}