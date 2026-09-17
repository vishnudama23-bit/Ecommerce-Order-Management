package com.example.ecommerce.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.ecommerce.dto.request.WishlistRequestDto;
import com.example.ecommerce.dto.response.WishlistResponseDto;
import com.example.ecommerce.entity.Wishlist;

@Mapper(componentModel = "spring")
public interface WishlistMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "addedDate", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "product", ignore = true)
    Wishlist toEntity(WishlistRequestDto request);

    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productPrice", source = "product.price")
    WishlistResponseDto toResponseDto(Wishlist wishlist);
}