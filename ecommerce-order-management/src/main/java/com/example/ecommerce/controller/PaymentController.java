package com.example.ecommerce.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecommerce.dto.request.PaymentRequestDto;
import com.example.ecommerce.dto.response.PaymentResponseDto;
import com.example.ecommerce.service.PaymentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(
            PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponseDto> createPayment(
            @Valid @RequestBody PaymentRequestDto request) {

        PaymentResponseDto response =
                paymentService.createOrUpdatePayment(
                        null,
                        request);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentResponseDto> updatePayment(
            @PathVariable Long id,
            @Valid @RequestBody PaymentRequestDto request) {

        PaymentResponseDto response =
                paymentService.createOrUpdatePayment(
                        id,
                        request);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponseDto>> getAllPayments() {

        List<PaymentResponseDto> response =
                paymentService.getAllPayments();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponseDto> getPaymentById(
            @PathVariable Long id) {

        PaymentResponseDto response =
                paymentService.getPaymentById(id);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(
            @PathVariable Long id) {

        paymentService.deletePayment(id);

        return ResponseEntity.noContent().build();
    }
}