package com.example.ecommerce.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ecommerce.entity.Order;
import com.example.ecommerce.enums.OrderStatus;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByCustomerId(Long customerId, Pageable pageable);

    List<Order> findByCustomerId(Long customerId);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByStatusAndOrderDateBefore(
            OrderStatus status,
            LocalDateTime dateTime);

    boolean existsByCustomerId(Long customerId);
}