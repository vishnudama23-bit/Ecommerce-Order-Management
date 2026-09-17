package com.example.ecommerce.dto.request;

import java.util.List;

import com.example.ecommerce.enums.OrderStatus;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDto {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    private OrderStatus status;

    @Valid
    private List<OrderItemRequestDto> items;

}