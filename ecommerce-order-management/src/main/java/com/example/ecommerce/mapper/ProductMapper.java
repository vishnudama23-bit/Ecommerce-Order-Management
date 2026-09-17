package com.example.ecommerce.mapper;

import org.mapstruct.Mapper;

import com.example.ecommerce.dto.request.ProductRequestDto;
import com.example.ecommerce.dto.response.ProductResponseDto;
import com.example.ecommerce.entity.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product toEntity(ProductRequestDto dto);

    ProductResponseDto toResponseDto(Product product);
}