package com.example.ecommerce.service;

import java.util.List;

import com.example.ecommerce.dto.request.CategoryRequestDto;
import com.example.ecommerce.dto.response.CategoryResponseDto;

public interface CategoryService {

    CategoryResponseDto createOrUpdateCategory(
            Long id,
            CategoryRequestDto request);

    List<CategoryResponseDto> getAllCategories();

    CategoryResponseDto getCategoryById(Long id);

    void deleteCategory(Long id);
}