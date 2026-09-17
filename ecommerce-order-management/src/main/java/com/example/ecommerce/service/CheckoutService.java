package com.example.ecommerce.service;

import com.example.ecommerce.dto.response.OrderResponseDto;

public interface CheckoutService {

    OrderResponseDto checkout(Long customerId);
}