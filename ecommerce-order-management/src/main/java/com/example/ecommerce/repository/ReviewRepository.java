package com.example.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ecommerce.entity.Review;

public interface ReviewRepository
        extends JpaRepository<Review, Long> {

    List<Review> findByProductId(Long productId);

    List<Review> findByCustomerId(Long customerId);
}