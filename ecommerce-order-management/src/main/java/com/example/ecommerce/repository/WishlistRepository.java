package com.example.ecommerce.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ecommerce.entity.Wishlist;

public interface WishlistRepository
        extends JpaRepository<Wishlist, Long> {

    boolean existsByCustomerIdAndProductId(
            Long customerId,
            Long productId);

    List<Wishlist> findByCustomerId(
            Long customerId);

    Optional<Wishlist> findByCustomerIdAndProductId(
            Long customerId,
            Long productId);
}