package com.example.ecommerce.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.ecommerce.dto.request.CategoryRequestDto;
import com.example.ecommerce.dto.response.CategoryResponseDto;
import com.example.ecommerce.entity.Category;
import com.example.ecommerce.exception.CategoryInUseException;
import com.example.ecommerce.exception.CategoryNotFoundException;
import com.example.ecommerce.mapper.CategoryMapper;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.impl.CategoryServiceImpl;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;
    private CategoryRequestDto request;
    private CategoryResponseDto responseDto;

    @BeforeEach
    void setUp() {

        category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        request = new CategoryRequestDto();
        request.setName("Electronics");

        responseDto = new CategoryResponseDto();
        responseDto.setId(1L);
        responseDto.setName("Electronics");
    }

    @Test
    void shouldCreateCategory() {

        when(categoryMapper.toEntity(request))
                .thenReturn(category);

        when(categoryRepository.save(category))
                .thenReturn(category);

        when(categoryMapper.toResponseDto(category))
                .thenReturn(responseDto);

        CategoryResponseDto result =
                categoryService.createOrUpdateCategory(
                        null,
                        request);

        assertEquals(
                1L,
                result.getId());

        assertEquals(
                "Electronics",
                result.getName());

        verify(categoryMapper)
                .toEntity(request);

        verify(categoryRepository)
                .save(category);

        verify(categoryMapper)
                .toResponseDto(category);
    }

    @Test
    void shouldUpdateCategory() {

        CategoryRequestDto updateRequest =
                new CategoryRequestDto();

        updateRequest.setName("Mobile Phones");

        CategoryResponseDto updateResponse =
                new CategoryResponseDto();

        updateResponse.setId(1L);
        updateResponse.setName("Mobile Phones");

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(categoryRepository.save(category))
                .thenReturn(category);

        when(categoryMapper.toResponseDto(category))
                .thenReturn(updateResponse);

        CategoryResponseDto result =
                categoryService.createOrUpdateCategory(
                        1L,
                        updateRequest);

        assertEquals(
                "Mobile Phones",
                category.getName());

        assertEquals(
                updateResponse,
                result);

        verify(categoryRepository)
                .findById(1L);

        verify(categoryRepository)
                .save(category);

        verify(categoryMapper)
                .toResponseDto(category);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingCategory() {

        when(categoryRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                CategoryNotFoundException.class,
                () -> categoryService.createOrUpdateCategory(
                        99L,
                        request));

        verify(categoryRepository)
                .findById(99L);

        verify(categoryRepository, never())
                .save(any(Category.class));

        verify(categoryMapper, never())
                .toResponseDto(any(Category.class));
    }

    @Test
    void shouldGetCategoryById() {

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(categoryMapper.toResponseDto(category))
                .thenReturn(responseDto);

        CategoryResponseDto result =
                categoryService.getCategoryById(1L);

        assertEquals(
                1L,
                result.getId());

        assertEquals(
                "Electronics",
                result.getName());

        verify(categoryRepository)
                .findById(1L);

        verify(categoryMapper)
                .toResponseDto(category);
    }

    @Test
    void shouldThrowExceptionWhenCategoryNotFound() {

        when(categoryRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                CategoryNotFoundException.class,
                () -> categoryService.getCategoryById(99L));

        verify(categoryRepository)
                .findById(99L);

        verify(categoryMapper, never())
                .toResponseDto(any(Category.class));
    }

    @Test
    void shouldGetAllCategories() {

        Category secondCategory = new Category();
        secondCategory.setId(2L);
        secondCategory.setName("Clothing");

        CategoryResponseDto secondResponse =
                new CategoryResponseDto();

        secondResponse.setId(2L);
        secondResponse.setName("Clothing");

        when(categoryRepository.findAll())
                .thenReturn(List.of(
                        category,
                        secondCategory));

        when(categoryMapper.toResponseDto(category))
                .thenReturn(responseDto);

        when(categoryMapper.toResponseDto(secondCategory))
                .thenReturn(secondResponse);

        List<CategoryResponseDto> result =
                categoryService.getAllCategories();

        assertEquals(
                2,
                result.size());

        assertEquals(
                1L,
                result.get(0).getId());

        assertEquals(
                2L,
                result.get(1).getId());

        assertEquals(
                "Electronics",
                result.get(0).getName());

        assertEquals(
                "Clothing",
                result.get(1).getName());

        verify(categoryRepository)
                .findAll();

        verify(categoryMapper)
                .toResponseDto(category);

        verify(categoryMapper)
                .toResponseDto(secondCategory);
    }

    @Test
    void shouldDeleteCategory() {

        when(categoryRepository.existsById(1L))
                .thenReturn(true);

        when(productRepository.existsByCategoryId(1L))
                .thenReturn(false);

        categoryService.deleteCategory(1L);

        verify(categoryRepository)
                .existsById(1L);

        verify(productRepository)
                .existsByCategoryId(1L);

        verify(categoryRepository)
                .deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingCategory() {

        when(categoryRepository.existsById(99L))
                .thenReturn(false);

        assertThrows(
                CategoryNotFoundException.class,
                () -> categoryService.deleteCategory(99L));

        verify(categoryRepository)
                .existsById(99L);

        verify(productRepository, never())
                .existsByCategoryId(99L);

        verify(categoryRepository, never())
                .deleteById(99L);
    }

    @Test
    void shouldThrowExceptionWhenCategoryIsInUse() {

        when(categoryRepository.existsById(1L))
                .thenReturn(true);

        when(productRepository.existsByCategoryId(1L))
                .thenReturn(true);

        assertThrows(
                CategoryInUseException.class,
                () -> categoryService.deleteCategory(1L));

        verify(categoryRepository)
                .existsById(1L);

        verify(productRepository)
                .existsByCategoryId(1L);

        verify(categoryRepository, never())
                .deleteById(1L);
    }
}