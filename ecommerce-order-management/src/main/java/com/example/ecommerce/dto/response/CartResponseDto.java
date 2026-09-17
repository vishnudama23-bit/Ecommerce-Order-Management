package com.example.ecommerce.dto.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponseDto {

    private Long id;

    private Long customerId;

    private List<CartItemResponseDto> cartItems;

    private BigDecimal totalAmount;
}