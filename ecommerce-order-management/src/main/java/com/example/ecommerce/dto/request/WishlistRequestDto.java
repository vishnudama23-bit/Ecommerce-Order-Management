package com.example.ecommerce.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WishlistRequestDto {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Product ID is required")
    private Long productId;
}