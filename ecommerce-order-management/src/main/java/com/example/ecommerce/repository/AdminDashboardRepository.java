package com.example.ecommerce.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.ecommerce.entity.Customer;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.Payment;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.enums.OrderStatus;

public interface AdminDashboardRepository extends JpaRepository<Order, Long> {

    @Query("SELECT COUNT(c) FROM Customer c")
    long countCustomers();

    @Query("SELECT COUNT(p) FROM Product p")
    long countProducts();

    @Query("SELECT COUNT(o) FROM Order o")
    long countOrders();

    @Query("SELECT COUNT(p) FROM Payment p")
    long countPayments();

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status <> :status")
    BigDecimal calculateTotalRevenue(
            @Param("status") OrderStatus status);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status IN :statuses")
    long countPendingOrders(
            @Param("statuses") List<OrderStatus> statuses);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    long countOrdersByStatus(
            @Param("status") OrderStatus status);
}