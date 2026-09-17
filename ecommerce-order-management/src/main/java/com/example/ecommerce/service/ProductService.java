package com.example.ecommerce.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.example.ecommerce.dto.request.ProductRequestDto;
import com.example.ecommerce.dto.response.ProductResponseDto;

public interface ProductService {

    ProductResponseDto createOrUpdateProduct(
            Long id,
            ProductRequestDto request);

    Page<ProductResponseDto> getAllProducts(
            Pageable pageable);

    ProductResponseDto getProductById(
            Long id);

    Page<ProductResponseDto> searchProducts(
            String name,
            Pageable pageable);

    ProductResponseDto updateProductImage(
            Long id,
            String imageUrl);

    void deleteProduct(
            Long id);

    List<ProductResponseDto> createProductsFromCsv(
            MultipartFile file);

    ProductResponseDto uploadProductImage(
            Long id,
            MultipartFile file);

    Page<ProductResponseDto> filterProducts(
            String name,
            Long categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean inStock,
            Pageable pageable);
}