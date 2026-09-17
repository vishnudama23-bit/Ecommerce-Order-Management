package com.example.ecommerce.exception;

public class DuplicateProductException
        extends RuntimeException {

    public DuplicateProductException(String message) {
        super(message);
    }

    public DuplicateProductException(
            String message,
            Throwable cause) {
        super(message, cause);
    }
}