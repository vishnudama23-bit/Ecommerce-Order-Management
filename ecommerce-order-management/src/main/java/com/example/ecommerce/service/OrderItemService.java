package com.example.ecommerce.service;

import java.util.List;

import com.example.ecommerce.dto.request.OrderItemRequestDto;
import com.example.ecommerce.dto.response.OrderItemResponseDto;

public interface OrderItemService {

    OrderItemResponseDto createOrderItem(
            Long orderId,
            OrderItemRequestDto request);

    List<OrderItemResponseDto> getAllOrderItems();

    OrderItemResponseDto getOrderItemById(Long id);

    void deleteOrderItem(Long id);
}