package com.example.ecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewSummaryResponseDto {

    private Long productId;
    private long totalReviews;
    private double averageRating;
    private long oneStar;
    private long twoStar;
    private long threeStar;
    private long fourStar;
    private long fiveStar;
}