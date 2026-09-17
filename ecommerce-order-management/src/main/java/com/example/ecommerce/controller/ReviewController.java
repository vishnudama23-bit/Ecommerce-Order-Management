package com.example.ecommerce.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecommerce.dto.request.ReviewRequestDto;
import com.example.ecommerce.dto.response.ReviewResponseDto;
import com.example.ecommerce.dto.response.ReviewSummaryResponseDto;
import com.example.ecommerce.service.ReviewService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<ReviewResponseDto> createReview(
            @Valid @RequestBody ReviewRequestDto request) {

        ReviewResponseDto response =
                reviewService.createReview(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsByProduct(
            @PathVariable Long productId) {

        List<ReviewResponseDto> response =
                reviewService.getReviewsByProduct(productId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsByCustomer(
            @PathVariable Long customerId) {

        List<ReviewResponseDto> response =
                reviewService.getReviewsByCustomer(customerId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long id) {

        reviewService.deleteReview(id);

        return ResponseEntity.noContent().build();
    }
    @GetMapping("/product/{productId}/summary")
    public ResponseEntity<ReviewSummaryResponseDto> getReviewSummary(
            @PathVariable Long productId) {

        ReviewSummaryResponseDto response =
                reviewService.getReviewSummary(productId);

        return ResponseEntity.ok(response);
    }
}