package com.example.ecommerce.service;

import java.util.List;

import com.example.ecommerce.dto.request.ReviewRequestDto;
import com.example.ecommerce.dto.response.ReviewResponseDto;
import com.example.ecommerce.dto.response.ReviewSummaryResponseDto;

public interface ReviewService {

    ReviewResponseDto createReview(ReviewRequestDto request);

    List<ReviewResponseDto> getReviewsByProduct(Long productId);

    List<ReviewResponseDto> getReviewsByCustomer(Long customerId);

    void deleteReview(Long id);
    ReviewSummaryResponseDto getReviewSummary(Long productId);
}