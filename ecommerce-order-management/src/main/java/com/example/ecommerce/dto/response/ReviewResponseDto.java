package com.example.ecommerce.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDto {

    private Long id;

    private Integer rating;

    private String comment;

    private LocalDateTime reviewDate;

    private Long productId;

    private Long customerId;
}