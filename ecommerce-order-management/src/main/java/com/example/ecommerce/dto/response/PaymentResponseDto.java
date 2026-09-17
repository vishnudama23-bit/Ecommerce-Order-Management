package com.example.ecommerce.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDto {

    private Long id;

    private LocalDateTime paymentDate;

    private BigDecimal amount;

    private String paymentMethod;

    private String status;

    private Long orderId;
}