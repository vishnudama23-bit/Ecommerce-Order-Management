package com.example.ecommerce.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponseDto {

    private Long id;

    private Long productId;

    private String productName;

    private BigDecimal price;

    private Integer quantity;

    private BigDecimal subtotal;
}