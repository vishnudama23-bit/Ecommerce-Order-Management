package com.example.ecommerce.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDto {

    private Long id;

    private String name;

    private String description;

    private BigDecimal price;

    private Integer quantity;

    private String imageUrl;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    private Long version;

    private CategoryResponseDto category;
}