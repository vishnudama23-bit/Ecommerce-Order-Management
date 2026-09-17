package com.example.ecommerce.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.ecommerce.dto.request.OrderItemRequestDto;
import com.example.ecommerce.dto.request.OrderRequestDto;
import com.example.ecommerce.dto.response.OrderResponseDto;
import com.example.ecommerce.enums.OrderStatus;

public interface OrderService {

    OrderResponseDto createOrUpdateOrder(
            Long id,
            OrderRequestDto request);

    OrderResponseDto getOrderById(Long id);

    List<OrderResponseDto> getAllOrders();

    Page<OrderResponseDto> getAllOrders(Pageable pageable);

    List<OrderResponseDto> getOrdersByCustomerId(Long customerId);

    List<OrderResponseDto> getOrdersByCustomer(Long customerId);

    Page<OrderResponseDto> getOrdersByCustomer(
            Long customerId,
            Pageable pageable);

    OrderResponseDto updateOrderStatus(
            Long id,
            OrderStatus newStatus);

    void deleteOrder(Long id);

    void cancelOrder(Long id);

    void cancelOrderAutomatically(Long id);

    OrderResponseDto addOrderItem(
            Long orderId,
            OrderItemRequestDto request);

    void removeOrderItem(
            Long orderId,
            Long orderItemId);
    
    List<OrderResponseDto> searchOrdersByStatus(OrderStatus status);
   
}
