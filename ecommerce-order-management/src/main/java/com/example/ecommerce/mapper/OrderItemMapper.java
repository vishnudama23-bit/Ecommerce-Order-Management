package com.example.ecommerce.mapper;

import org.mapstruct.Mapper;

import com.example.ecommerce.dto.request.OrderItemRequestDto;
import com.example.ecommerce.dto.response.OrderItemResponseDto;
import com.example.ecommerce.entity.OrderItem;

@Mapper(
        componentModel = "spring",
        uses = {
                ProductMapper.class,
                OrderMapper.class
        }
)
public interface OrderItemMapper {

    OrderItem toEntity(OrderItemRequestDto dto);

    OrderItemResponseDto toResponseDto(
            OrderItem orderItem);
}