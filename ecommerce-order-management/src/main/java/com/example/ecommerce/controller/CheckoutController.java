package com.example.ecommerce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.ecommerce.dto.response.OrderResponseDto;
import com.example.ecommerce.service.CheckoutService;

@RestController
@RequestMapping("/checkout")
public class CheckoutController {

    private final CheckoutService checkoutService;

    public CheckoutController(
            CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @PostMapping("/{customerId}")
    public ResponseEntity<OrderResponseDto> checkout(
            @PathVariable Long customerId) {

        OrderResponseDto response =
                checkoutService.checkout(customerId);

        return ResponseEntity.ok(response);
    }
}