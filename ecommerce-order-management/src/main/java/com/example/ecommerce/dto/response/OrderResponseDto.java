package com.example.ecommerce.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {

    private Long id;

    private Long customerId;

    private LocalDateTime orderDate;

    private String status;

    private BigDecimal totalAmount;

    private List<OrderItemResponseDto> orderItems;

}