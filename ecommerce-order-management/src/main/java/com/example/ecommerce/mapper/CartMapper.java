package com.example.ecommerce.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.ecommerce.dto.response.CartItemResponseDto;
import com.example.ecommerce.dto.response.CartResponseDto;
import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.CartItem;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(target = "subtotal", ignore = true)
    CartItemResponseDto toCartItemResponseDto(
            CartItem cartItem);

    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(target = "cartItems", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    CartResponseDto toCartResponseDto(
            Cart cart);
}