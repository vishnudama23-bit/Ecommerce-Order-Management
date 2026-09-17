package com.example.ecommerce.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.example.ecommerce.dto.request.ProductRequestDto;
import com.example.ecommerce.dto.response.ProductResponseDto;
import com.example.ecommerce.entity.Category;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.exception.CategoryNotFoundException;
import com.example.ecommerce.exception.ProductNotFoundException;
import com.example.ecommerce.mapper.ProductMapper;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.impl.ProductServiceImpl;

public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private CategoryRepository categoryRepository;

    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        productService =
                new ProductServiceImpl(
                        productRepository,
                        productMapper,
                        categoryRepository);
    }

    @Test
    void shouldGetProductById() {

        Product product = new Product();
        product.setId(1L);
        product.setName("Samsung S24");
        product.setPrice(new BigDecimal("75000"));

        ProductResponseDto responseDto =
                new ProductResponseDto();

        responseDto.setId(1L);
        responseDto.setName("Samsung S24");
        responseDto.setPrice(
                new BigDecimal("75000"));

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(productMapper.toResponseDto(product))
                .thenReturn(responseDto);

        ProductResponseDto result =
                productService.getProductById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(
                "Samsung S24",
                result.getName());
        assertEquals(
                new BigDecimal("75000"),
                result.getPrice());
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {

        when(productRepository.findById(999L))
                .thenReturn(Optional.empty());

        ProductNotFoundException exception =
                assertThrows(
                        ProductNotFoundException.class,
                        () -> productService
                                .getProductById(999L));

        assertEquals(
                "Product not found with id: 999",
                exception.getMessage());
    }

    @Test
    void shouldDeleteProduct() {

        when(productRepository.existsById(1L))
                .thenReturn(true);

        productService.deleteProduct(1L);

        verify(productRepository)
                .deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingProduct() {

        when(productRepository.existsById(999L))
                .thenReturn(false);

        ProductNotFoundException exception =
                assertThrows(
                        ProductNotFoundException.class,
                        () -> productService
                                .deleteProduct(999L));

        assertEquals(
                "Product not found with id: 999",
                exception.getMessage());
    }

    @Test
    void shouldCreateProduct() {

        ProductRequestDto request =
                new ProductRequestDto();

        request.setName("iPhone 16");
        request.setDescription("Apple smartphone");
        request.setPrice(
                new BigDecimal("80000"));
        request.setQuantity(10);
        request.setCategoryId(1L);

        Category category = new Category();
        category.setId(1L);
        category.setName("smart phones");

        Product product = new Product();
        product.setName("iPhone 16");
        product.setDescription("Apple smartphone");
        product.setPrice(
                new BigDecimal("80000"));
        product.setQuantity(10);

        ProductResponseDto responseDto =
                new ProductResponseDto();

        responseDto.setId(10L);
        responseDto.setName("iPhone 16");
        responseDto.setPrice(
                new BigDecimal("80000"));
        responseDto.setQuantity(10);

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(productMapper.toEntity(request))
                .thenReturn(product);

        when(productRepository.save(product))
                .thenReturn(product);

        when(productMapper.toResponseDto(product))
                .thenReturn(responseDto);

        ProductResponseDto result =
                productService.createOrUpdateProduct(
                        null,
                        request);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals(
                "iPhone 16",
                result.getName());
        assertEquals(
                new BigDecimal("80000"),
                result.getPrice());

        verify(categoryRepository)
                .findById(1L);

        verify(productMapper)
                .toEntity(request);

        verify(productRepository)
                .save(product);
    }

    @Test
    void shouldUpdateProduct() {

        ProductRequestDto request =
                new ProductRequestDto();

        request.setName("Samsung S25");
        request.setDescription("Updated smartphone");
        request.setPrice(
                new BigDecimal("85000"));
        request.setQuantity(15);
        request.setCategoryId(1L);

        Category category = new Category();
        category.setId(1L);
        category.setName("smart phones");

        Product product = new Product();
        product.setId(1L);
        product.setName("Samsung S24");
        product.setDescription("Samsung smartphone");
        product.setPrice(
                new BigDecimal("75000"));
        product.setQuantity(10);

        ProductResponseDto responseDto =
                new ProductResponseDto();

        responseDto.setId(1L);
        responseDto.setName("Samsung S25");
        responseDto.setPrice(
                new BigDecimal("85000"));
        responseDto.setQuantity(15);

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(productRepository.save(product))
                .thenReturn(product);

        when(productMapper.toResponseDto(product))
                .thenReturn(responseDto);

        ProductResponseDto result =
                productService.createOrUpdateProduct(
                        1L,
                        request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(
                "Samsung S25",
                result.getName());
        assertEquals(
                new BigDecimal("85000"),
                result.getPrice());
        assertEquals(15, result.getQuantity());

        verify(productRepository)
                .findById(1L);

        verify(productRepository)
                .save(product);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingProduct() {

        ProductRequestDto request =
                new ProductRequestDto();

        request.setName("Samsung S25");
        request.setDescription("Updated smartphone");
        request.setPrice(
                new BigDecimal("85000"));
        request.setQuantity(15);
        request.setCategoryId(1L);

        Category category = new Category();
        category.setId(1L);

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(productRepository.findById(999L))
                .thenReturn(Optional.empty());

        ProductNotFoundException exception =
                assertThrows(
                        ProductNotFoundException.class,
                        () -> productService
                                .createOrUpdateProduct(
                                        999L,
                                        request));

        assertEquals(
                "Product not found with id: 999",
                exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCategoryNotFound() {

        ProductRequestDto request =
                new ProductRequestDto();

        request.setName("iPhone 16");
        request.setDescription("Apple smartphone");
        request.setPrice(
                new BigDecimal("80000"));
        request.setQuantity(10);
        request.setCategoryId(999L);

        when(categoryRepository.findById(999L))
                .thenReturn(Optional.empty());

        CategoryNotFoundException exception =
                assertThrows(
                        CategoryNotFoundException.class,
                        () -> productService
                                .createOrUpdateProduct(
                                        null,
                                        request));

        assertEquals(
                "Category not found with id: 999",
                exception.getMessage());
    }

    @Test
    void shouldSearchProducts() {

        Pageable pageable =
                PageRequest.of(0, 10);

        Product product = new Product();
        product.setId(1L);
        product.setName("Samsung S24");
        product.setPrice(
                new BigDecimal("75000"));

        ProductResponseDto responseDto =
                new ProductResponseDto();

        responseDto.setId(1L);
        responseDto.setName("Samsung S24");
        responseDto.setPrice(
                new BigDecimal("75000"));

        Page<Product> productPage =
                new PageImpl<>(
                        List.of(product),
                        pageable,
                        1);

        when(productRepository
                .findByNameContainingIgnoreCase(
                        "Samsung",
                        pageable))
                .thenReturn(productPage);

        when(productMapper.toResponseDto(product))
                .thenReturn(responseDto);

        Page<ProductResponseDto> result =
                productService.searchProducts(
                        "Samsung",
                        pageable);

        assertNotNull(result);
        assertEquals(
                1,
                result.getTotalElements());
        assertEquals(
                "Samsung S24",
                result.getContent()
                        .get(0)
                        .getName());

        verify(productRepository)
                .findByNameContainingIgnoreCase(
                        "Samsung",
                        pageable);
    }

    @Test
    void shouldGetAllProducts() {

        Pageable pageable =
                PageRequest.of(0, 10);

        Product product = new Product();
        product.setId(1L);
        product.setName("Samsung S24");
        product.setPrice(
                new BigDecimal("75000"));

        ProductResponseDto responseDto =
                new ProductResponseDto();

        responseDto.setId(1L);
        responseDto.setName("Samsung S24");
        responseDto.setPrice(
                new BigDecimal("75000"));

        Page<Product> productPage =
                new PageImpl<>(
                        List.of(product),
                        pageable,
                        1);

        when(productRepository.findAll(pageable))
                .thenReturn(productPage);

        when(productMapper.toResponseDto(product))
                .thenReturn(responseDto);

        Page<ProductResponseDto> result =
                productService.getAllProducts(
                        pageable);

        assertNotNull(result);
        assertEquals(
                1,
                result.getTotalElements());
        assertEquals(
                "Samsung S24",
                result.getContent()
                        .get(0)
                        .getName());

        verify(productRepository)
                .findAll(pageable);
    }

    @Test
    void shouldUpdateProductImage() {

        Product product = new Product();
        product.setId(1L);
        product.setName("Samsung S24");

        ProductResponseDto responseDto =
                new ProductResponseDto();

        responseDto.setId(1L);
        responseDto.setName("Samsung S24");
        responseDto.setImageUrl(
                "/images/products/test.png");

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(productRepository.save(product))
                .thenReturn(product);

        when(productMapper.toResponseDto(product))
                .thenReturn(responseDto);

        ProductResponseDto result =
                productService.updateProductImage(
                        1L,
                        "/images/products/test.png");

        assertNotNull(result);
        assertEquals(
                "/images/products/test.png",
                result.getImageUrl());

        verify(productRepository)
                .findById(1L);

        verify(productRepository)
                .save(product);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingImageForNonExistingProduct() {

        when(productRepository.findById(999L))
                .thenReturn(Optional.empty());

        ProductNotFoundException exception =
                assertThrows(
                        ProductNotFoundException.class,
                        () -> productService
                                .updateProductImage(
                                        999L,
                                        "/images/products/test.png"));

        assertEquals(
                "Product not found with id: 999",
                exception.getMessage());
    }
}