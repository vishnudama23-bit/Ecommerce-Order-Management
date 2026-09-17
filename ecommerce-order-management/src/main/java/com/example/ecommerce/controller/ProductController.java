package com.example.ecommerce.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.ecommerce.dto.request.ProductRequestDto;
import com.example.ecommerce.dto.response.ProductResponseDto;
import com.example.ecommerce.service.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(
            ProductService productService) {

        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(
            @Valid @RequestBody ProductRequestDto request) {

        ProductResponseDto response =
                productService.createOrUpdateProduct(
                        null,
                        request);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDto request) {

        ProductResponseDto response =
                productService.createOrUpdateProduct(
                        id,
                        request);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponseDto>> getAllProducts(
            Pageable pageable) {

        Page<ProductResponseDto> response =
                productService.getAllProducts(
                        pageable);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponseDto>> searchProducts(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortDirection) {

        Sort.Direction direction = Sort.Direction.ASC;

        if (sortDirection != null
                && sortDirection.equalsIgnoreCase("DESC")) {

            direction = Sort.Direction.DESC;
        }

        Pageable pageable;

        if (sortField != null
                && !sortField.isBlank()) {

            pageable = PageRequest.of(
                    page,
                    size,
                    Sort.by(
                            direction,
                            sortField));
        } else {

            pageable = PageRequest.of(
                    page,
                    size);
        }

        Page<ProductResponseDto> response =
                productService.searchProducts(
                        name,
                        pageable);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<ProductResponseDto>> filterProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortDirection,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Sort.Direction direction = Sort.Direction.ASC;

        if (sortDirection != null
                && sortDirection.equalsIgnoreCase("DESC")) {

            direction = Sort.Direction.DESC;
        }

        Pageable pageable;

        if (sortField != null
                && !sortField.isBlank()) {

            pageable = PageRequest.of(
                    page,
                    size,
                    Sort.by(
                            direction,
                            sortField));
        } else {

            pageable = PageRequest.of(
                    page,
                    size);
        }

        Page<ProductResponseDto> response =
                productService.filterProducts(
                        name,
                        categoryId,
                        minPrice,
                        maxPrice,
                        inStock,
                        pageable);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(
            @PathVariable Long id) {

        ProductResponseDto response =
                productService.getProductById(id);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/image")
    public ResponseEntity<ProductResponseDto> updateProductImage(
            @PathVariable Long id,
            @RequestParam String imageUrl) {

        ProductResponseDto response =
                productService.updateProductImage(
                        id,
                        imageUrl);

        return ResponseEntity.ok(response);
    }

    @PostMapping(
            value = "/{id}/image/upload",
            consumes = "multipart/form-data")
    public ResponseEntity<ProductResponseDto> uploadProductImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        ProductResponseDto response =
                productService.uploadProductImage(
                        id,
                        file);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id) {

        productService.deleteProduct(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping(
            value = "/upload-csv",
            consumes = "multipart/form-data")
    public ResponseEntity<List<ProductResponseDto>> uploadProductsFromCsv(
            @RequestParam("file") MultipartFile file) {

        List<ProductResponseDto> response =
                productService.createProductsFromCsv(
                        file);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}