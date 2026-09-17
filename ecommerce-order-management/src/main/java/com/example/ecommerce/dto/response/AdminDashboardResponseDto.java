package com.example.ecommerce.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardResponseDto {

    private long totalCustomers;
    private long totalProducts;
    private long totalOrders;
    private long totalPayments;
    private BigDecimal totalRevenue;
    private long pendingOrders;
    private long deliveredOrders;
}