package com.example.ecommerce.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ecommerce.dto.request.ReviewRequestDto;
import com.example.ecommerce.dto.response.ReviewResponseDto;
import com.example.ecommerce.dto.response.ReviewSummaryResponseDto;
import com.example.ecommerce.entity.Customer;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.Review;
import com.example.ecommerce.exception.CustomerNotFoundException;
import com.example.ecommerce.exception.InvalidOrderException;
import com.example.ecommerce.exception.ProductNotFoundException;
import com.example.ecommerce.mapper.ReviewMapper;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.ReviewRepository;
import com.example.ecommerce.service.ReviewService;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final ReviewMapper reviewMapper;

    public ReviewServiceImpl(
            ReviewRepository reviewRepository,
            ProductRepository productRepository,
            CustomerRepository customerRepository,
            ReviewMapper reviewMapper) {

        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.reviewMapper = reviewMapper;
    }

    @Override
    @Transactional
    public ReviewResponseDto createReview(
            ReviewRequestDto request) {

        Product product =
                productRepository.findById(request.getProductId())
                        .orElseThrow(() ->
                                new ProductNotFoundException(
                                        "Product not found with id: "
                                                + request.getProductId()));

        Customer customer =
                customerRepository.findById(request.getCustomerId())
                        .orElseThrow(() ->
                                new CustomerNotFoundException(
                                        "Customer not found with id: "
                                                + request.getCustomerId()));

        Review review =
                reviewMapper.toEntity(request);

        review.setProduct(product);
        review.setCustomer(customer);
        review.setReviewDate(LocalDateTime.now());

        Review savedReview =
                reviewRepository.save(review);

        return reviewMapper.toResponseDto(savedReview);
    }

    @Override
    public List<ReviewResponseDto> getReviewsByProduct(
            Long productId) {

        productRepository.findById(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with id: "
                                        + productId));

        List<Review> reviews =
                reviewRepository.findByProductId(productId);

        return reviews.stream()
                .map(reviewMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<ReviewResponseDto> getReviewsByCustomer(
            Long customerId) {

        customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new CustomerNotFoundException(
                                "Customer not found with id: "
                                        + customerId));

        List<Review> reviews =
                reviewRepository.findByCustomerId(customerId);

        return reviews.stream()
                .map(reviewMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public void deleteReview(Long id) {

        Review review =
                reviewRepository.findById(id)
                        .orElseThrow(() ->
                                new InvalidOrderException(
                                        "Review not found with id: "
                                                + id));

        reviewRepository.delete(review);
    }
    @Override
    public ReviewSummaryResponseDto getReviewSummary(Long productId) {

        productRepository.findById(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with id: "
                                        + productId));

        List<Review> reviews =
                reviewRepository.findByProductId(productId);

        long oneStar = reviews.stream()
                .filter(review -> review.getRating() == 1)
                .count();

        long twoStar = reviews.stream()
                .filter(review -> review.getRating() == 2)
                .count();

        long threeStar = reviews.stream()
                .filter(review -> review.getRating() == 3)
                .count();

        long fourStar = reviews.stream()
                .filter(review -> review.getRating() == 4)
                .count();

        long fiveStar = reviews.stream()
                .filter(review -> review.getRating() == 5)
                .count();

        long totalReviews = reviews.size();

        double averageRating = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        return new ReviewSummaryResponseDto(
                productId,
                totalReviews,
                averageRating,
                oneStar,
                twoStar,
                threeStar,
                fourStar,
                fiveStar);
    }
}