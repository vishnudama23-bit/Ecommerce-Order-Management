package com.example.ecommerce.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDto {

    @NotBlank(message = "Product name is required")
    private String name;

    private String description;

    @DecimalMin(
            value = "0.01",
            message = "Price must be greater than 0")
    private BigDecimal price;

    @Min(
            value = 0,
            message = "Quantity cannot be negative")
    private Integer quantity;

    private Long categoryId;

    private String imageUrl;
}