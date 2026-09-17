package com.example.ecommerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ecommerce.entity.Cart;

public interface CartRepository
        extends JpaRepository<Cart, Long> {

    Optional<Cart> findByCustomerId(Long customerId);

    boolean existsByCustomerId(Long customerId);
}