package com.example.ecommerce.mapper;

import org.mapstruct.Mapper;

import com.example.ecommerce.dto.request.CategoryRequestDto;
import com.example.ecommerce.dto.response.CategoryResponseDto;
import com.example.ecommerce.entity.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toEntity(CategoryRequestDto dto);

    CategoryResponseDto toResponseDto(Category category);
}