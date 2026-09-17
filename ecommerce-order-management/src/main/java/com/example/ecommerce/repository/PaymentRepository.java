package com.example.ecommerce.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ecommerce.entity.Payment;

public interface PaymentRepository
        extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderId(Long orderId);

    boolean existsByOrderId(Long orderId);

    void deleteByOrderId(Long orderId);

    List<Payment> findByOrderCustomerUserEmail(String email);
}