package com.example.ecommerce.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.ecommerce.dto.request.ReviewRequestDto;
import com.example.ecommerce.dto.response.ReviewResponseDto;
import com.example.ecommerce.entity.Review;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reviewDate", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "customer", ignore = true)
    Review toEntity(ReviewRequestDto request);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "customerId", source = "customer.id")
    ReviewResponseDto toResponseDto(Review review);
}