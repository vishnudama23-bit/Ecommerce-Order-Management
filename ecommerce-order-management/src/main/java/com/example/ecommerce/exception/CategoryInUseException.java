package com.example.ecommerce.exception;

public class CategoryInUseException extends RuntimeException {

    public CategoryInUseException(String message) {
        super(message);
    }
}