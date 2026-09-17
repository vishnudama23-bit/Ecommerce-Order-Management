package com.example.ecommerce.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WishlistResponseDto {

    private Long id;
    private Long customerId;
    private Long productId;
    private String productName;
    private BigDecimal productPrice;
    private LocalDateTime addedDate;
}