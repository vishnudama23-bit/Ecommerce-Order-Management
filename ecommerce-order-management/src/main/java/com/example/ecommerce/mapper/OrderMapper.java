package com.example.ecommerce.mapper;

import java.math.BigDecimal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.ecommerce.dto.request.OrderRequestDto;
import com.example.ecommerce.dto.response.OrderItemResponseDto;
import com.example.ecommerce.dto.response.OrderResponseDto;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.OrderItem;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    Order toEntity(OrderRequestDto dto);

    @Mapping(
            target = "customerId",
            source = "customer.id")
    @Mapping(
            target = "status",
            source = "status")
    OrderResponseDto toResponseDto(Order order);

    @Mapping(
            target = "productId",
            source = "product.id")
    @Mapping(
            target = "productName",
            source = "product.name")
    @Mapping(
            target = "subtotal",
            expression = "java(calculateSubtotal(orderItem))")
    OrderItemResponseDto toOrderItemResponseDto(
            OrderItem orderItem);

    default BigDecimal calculateSubtotal(
            OrderItem orderItem) {

        if (orderItem.getPrice() == null
                || orderItem.getQuantity() == null) {
            return BigDecimal.ZERO;
        }

        return orderItem.getPrice()
                .multiply(
                        BigDecimal.valueOf(
                                orderItem.getQuantity()));
    }
}