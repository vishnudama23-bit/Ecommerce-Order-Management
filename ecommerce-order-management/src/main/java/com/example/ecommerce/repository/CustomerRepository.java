package com.example.ecommerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ecommerce.entity.Customer;

public interface CustomerRepository
        extends JpaRepository<Customer, Long> {

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    boolean existsByEmailAndIdNot(
            String email,
            Long id);

    boolean existsByPhoneAndIdNot(
            String phone,
            Long id);

    Optional<Customer> findByUserEmail(String email);

    boolean existsByUserEmail(String email);
}