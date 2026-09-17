package com.example.ecommerce.exception;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFound(
            ProductNotFoundException ex) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage());
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCategoryNotFound(
            CategoryNotFoundException ex) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage());
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCustomerNotFound(
            CustomerNotFoundException ex) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage());
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFound(
            OrderNotFoundException ex) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage());
    }

    @ExceptionHandler(OrderItemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderItemNotFound(
            OrderItemNotFoundException ex) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage());
    }

    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePaymentNotFound(
            PaymentNotFoundException ex) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage());
    }

    @ExceptionHandler(DuplicateProductException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateProduct(
            DuplicateProductException ex) {

        return buildResponse(
                HttpStatus.CONFLICT,
                ex.getMessage());
    }

    @ExceptionHandler(CsvFileException.class)
    public ResponseEntity<ErrorResponse> handleCsvFile(
            CsvFileException ex) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage());
    }

    @ExceptionHandler(CategoryInUseException.class)
    public ResponseEntity<ErrorResponse> handleCategoryInUse(
            CategoryInUseException ex) {

        return buildResponse(
                HttpStatus.CONFLICT,
                ex.getMessage());
    }

    @ExceptionHandler(InvalidOrderStatusException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOrderStatus(
            InvalidOrderStatusException ex) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage());
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStock(
            InsufficientStockException ex) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage());
    }

    @ExceptionHandler(InvalidPaymentException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPayment(
            InvalidPaymentException ex) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage());
    }

    @ExceptionHandler(InvalidOrderException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOrder(
            InvalidOrderException ex) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage());
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEmail(
            DuplicateEmailException ex) {

        return buildResponse(
                HttpStatus.CONFLICT,
                ex.getMessage());
    }

    @ExceptionHandler(DuplicatePhoneException.class)
    public ResponseEntity<ErrorResponse> handleDuplicatePhone(
            DuplicatePhoneException ex) {

        return buildResponse(
                HttpStatus.CONFLICT,
                ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex) {

        String message =
                ex.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(error ->
                                error.getField()
                                        + ": "
                                        + error.getDefaultMessage())
                        .collect(Collectors.joining(", "));

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(
            Exception ex) {

        ex.printStackTrace();

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage());
    }

    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status,
            String message) {

        ErrorResponse errorResponse =
                new ErrorResponse(
                        LocalDateTime.now(),
                        status.value(),
                        message);

        return ResponseEntity
                .status(status)
                .body(errorResponse);
    }
}