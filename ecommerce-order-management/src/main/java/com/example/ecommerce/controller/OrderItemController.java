package com.example.ecommerce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecommerce.dto.request.OrderItemRequestDto;
import com.example.ecommerce.dto.response.OrderResponseDto;
import com.example.ecommerce.service.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/orders")
public class OrderItemController {

    private final OrderService orderService;

    public OrderItemController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/{orderId}/items")
    public ResponseEntity<OrderResponseDto> createOrderItem(
            @PathVariable Long orderId,
            @Valid @RequestBody OrderItemRequestDto request) {

        OrderResponseDto response =
                orderService.addOrderItem(
                        orderId,
                        request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{orderId}/items/{orderItemId}")
    public ResponseEntity<Void> deleteOrderItem(
            @PathVariable Long orderId,
            @PathVariable Long orderItemId) {

        orderService.removeOrderItem(
                orderId,
                orderItemId);

        return ResponseEntity
                .noContent()
                .build();
    }

}