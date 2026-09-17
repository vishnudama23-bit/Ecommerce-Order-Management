package com.example.ecommerce.service;

import java.util.List;

import com.example.ecommerce.dto.request.PaymentRequestDto;
import com.example.ecommerce.dto.response.PaymentResponseDto;

public interface PaymentService {

    PaymentResponseDto createOrUpdatePayment(
            Long id,
            PaymentRequestDto request);

    List<PaymentResponseDto> getAllPayments();

    PaymentResponseDto getPaymentById(
            Long id);

    void deletePayment(
            Long id);
}