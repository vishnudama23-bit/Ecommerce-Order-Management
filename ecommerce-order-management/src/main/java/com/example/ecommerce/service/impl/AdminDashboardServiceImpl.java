package com.example.ecommerce.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.ecommerce.dto.response.AdminDashboardResponseDto;
import com.example.ecommerce.enums.OrderStatus;
import com.example.ecommerce.repository.AdminDashboardRepository;
import com.example.ecommerce.service.AdminDashboardService;

@Service
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final AdminDashboardRepository adminDashboardRepository;

    public AdminDashboardServiceImpl(
            AdminDashboardRepository adminDashboardRepository) {
        this.adminDashboardRepository = adminDashboardRepository;
    }

    @Override
    public AdminDashboardResponseDto getDashboard() {

        long totalCustomers =
                adminDashboardRepository.countCustomers();

        long totalProducts =
                adminDashboardRepository.countProducts();

        long totalOrders =
                adminDashboardRepository.countOrders();

        long totalPayments =
                adminDashboardRepository.countPayments();

        BigDecimal totalRevenue =
                adminDashboardRepository.calculateTotalRevenue(
                        OrderStatus.CANCELLED);

        List<OrderStatus> pendingStatuses = List.of(
                OrderStatus.PLACED,
                OrderStatus.PAID,
                OrderStatus.CONFIRMED,
                OrderStatus.SHIPPED
        );

        long pendingOrders =
                adminDashboardRepository.countPendingOrders(
                        pendingStatuses);

        long deliveredOrders =
                adminDashboardRepository.countOrdersByStatus(
                        OrderStatus.DELIVERED);

        return new AdminDashboardResponseDto(
                totalCustomers,
                totalProducts,
                totalOrders,
                totalPayments,
                totalRevenue,
                pendingOrders,
                deliveredOrders
        );
    }
}