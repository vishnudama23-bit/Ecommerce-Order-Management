package com.example.ecommerce.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.ecommerce.dto.request.ProductRequestDto;
import com.example.ecommerce.dto.response.ProductResponseDto;
import com.example.ecommerce.entity.Category;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.exception.CategoryNotFoundException;
import com.example.ecommerce.exception.CsvFileException;
import com.example.ecommerce.exception.DuplicateProductException;
import com.example.ecommerce.exception.ProductNotFoundException;
import com.example.ecommerce.mapper.ProductMapper;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.ProductSpecification;
import com.example.ecommerce.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    private final CategoryRepository categoryRepository;

    public ProductServiceImpl(
            ProductRepository productRepository,
            ProductMapper productMapper,
            CategoryRepository categoryRepository) {

        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public ProductResponseDto createOrUpdateProduct(
            Long id,
            ProductRequestDto request) {

        String name =
                request.getName().trim();

        Category category =
                categoryRepository.findById(
                        request.getCategoryId())
                        .orElseThrow(() ->
                                new CategoryNotFoundException(
                                        "Category not found with id: "
                                                + request.getCategoryId()));

        if (id == null) {

            if (productRepository
                    .existsByNameIgnoreCase(name)) {

                throw new DuplicateProductException(
                        "Product already exists with name: "
                                + name);
            }

            Product product =
                    productMapper.toEntity(request);

            product.setName(name);

            product.setCategory(category);

            Product savedProduct =
                    productRepository.save(product);

            return productMapper.toResponseDto(
                    savedProduct);
        }

        Product product =
                productRepository.findById(id)
                        .orElseThrow(() ->
                                new ProductNotFoundException(
                                        "Product not found with id: "
                                                + id));

        if (!product.getName()
                .equalsIgnoreCase(name)
                && productRepository
                        .existsByNameIgnoreCase(name)) {

            throw new DuplicateProductException(
                    "Product already exists with name: "
                            + name);
        }

        product.setName(name);

        product.setDescription(
                request.getDescription());

        product.setPrice(
                request.getPrice());

        product.setQuantity(
                request.getQuantity());

        product.setImageUrl(
                request.getImageUrl());

        product.setCategory(category);

        Product updatedProduct =
                productRepository.save(product);

        return productMapper.toResponseDto(
                updatedProduct);
    }

    @Override
    public Page<ProductResponseDto> getAllProducts(
            Pageable pageable) {

        Page<Product> products =
                productRepository.findAll(pageable);

        return products.map(
                productMapper::toResponseDto);
    }

    @Override
    public ProductResponseDto getProductById(
            Long id) {

        Product product =
                productRepository.findById(id)
                        .orElseThrow(() ->
                                new ProductNotFoundException(
                                        "Product not found with id: "
                                                + id));

        return productMapper.toResponseDto(
                product);
    }

    @Override
    public Page<ProductResponseDto> searchProducts(
            String name,
            Pageable pageable) {

        Page<Product> products =
                productRepository
                        .findByNameContainingIgnoreCase(
                                name,
                                pageable);

        return products.map(
                productMapper::toResponseDto);
    }

    @Override
    public ProductResponseDto updateProductImage(
            Long id,
            String imageUrl) {

        Product product =
                productRepository.findById(id)
                        .orElseThrow(() ->
                                new ProductNotFoundException(
                                        "Product not found with id: "
                                                + id));

        product.setImageUrl(imageUrl);

        Product updatedProduct =
                productRepository.save(product);

        return productMapper.toResponseDto(
                updatedProduct);
    }

    @Override
    public void deleteProduct(
            Long id) {

        Product product =
                productRepository.findById(id)
                        .orElseThrow(() ->
                                new ProductNotFoundException(
                                        "Product not found with id: "
                                                + id));

        productRepository.delete(product);
    }

    @Override
    @Transactional
    public List<ProductResponseDto> createProductsFromCsv(
            MultipartFile file) {

        validateFile(file);

        List<Product> products =
                new ArrayList<>();

        Set<String> csvProductNames =
                new HashSet<>();

        try (
                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        file.getInputStream(),
                                        StandardCharsets.UTF_8))
        ) {

            String header =
                    reader.readLine();

            validateHeader(header);

            String line;

            int rowNumber = 1;

            while ((line = reader.readLine()) != null) {

                rowNumber++;

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] columns =
                        parseCsvLine(line);

                if (columns.length != 5) {

                    throw new CsvFileException(
                            "Invalid CSV data at row "
                                    + rowNumber
                                    + ". Expected 5 columns.");
                }

                String name =
                        columns[0].trim();

                String description =
                        columns[1].trim();

                String priceValue =
                        columns[2].trim();

                String quantityValue =
                        columns[3].trim();

                String categoryIdValue =
                        columns[4].trim();

                if (name.isEmpty()) {

                    throw new CsvFileException(
                            "Product name is required at row "
                                    + rowNumber);
                }

                String normalizedName =
                        name.toLowerCase(
                                Locale.ROOT);

                if (!csvProductNames.add(
                        normalizedName)) {

                    throw new DuplicateProductException(
                            "Duplicate product in CSV at row "
                                    + rowNumber
                                    + ": "
                                    + name);
                }

                if (productRepository
                        .existsByNameIgnoreCase(name)) {

                    throw new DuplicateProductException(
                            "Product already exists with name: "
                                    + name);
                }

                BigDecimal price;

                try {

                    price =
                            new BigDecimal(
                                    priceValue);

                } catch (NumberFormatException ex) {

                    throw new CsvFileException(
                            "Invalid price at row "
                                    + rowNumber
                                    + ": "
                                    + priceValue);
                }

                if (price.compareTo(
                        BigDecimal.ZERO) < 0) {

                    throw new CsvFileException(
                            "Price cannot be negative at row "
                                    + rowNumber);
                }

                Integer quantity;

                try {

                    quantity =
                            Integer.valueOf(
                                    quantityValue);

                } catch (NumberFormatException ex) {

                    throw new CsvFileException(
                            "Invalid quantity at row "
                                    + rowNumber
                                    + ": "
                                    + quantityValue);
                }

                if (quantity < 0) {

                    throw new CsvFileException(
                            "Quantity cannot be negative at row "
                                    + rowNumber);
                }

                Long categoryId;

                try {

                    categoryId =
                            Long.valueOf(
                                    categoryIdValue);

                } catch (NumberFormatException ex) {

                    throw new CsvFileException(
                            "Invalid category ID at row "
                                    + rowNumber
                                    + ": "
                                    + categoryIdValue);
                }

                Category category =
                        categoryRepository
                                .findById(categoryId)
                                .orElse(null);

                if (category == null) {

                    throw new CategoryNotFoundException(
                            "Category not found with id: "
                                    + categoryId
                                    + " at row "
                                    + rowNumber);
                }

                Product product =
                        new Product();

                product.setName(name);

                product.setDescription(
                        description);

                product.setPrice(price);

                product.setQuantity(quantity);

                product.setCategory(category);

                products.add(product);
            }

            if (products.isEmpty()) {

                throw new CsvFileException(
                        "CSV file does not contain any products");
            }

            List<Product> savedProducts;

            try {

                savedProducts =
                        productRepository
                                .saveAllAndFlush(products);

            } catch (DataIntegrityViolationException ex) {

                throw new DuplicateProductException(
                        "One or more product names already exist",
                        ex);
            }

            return savedProducts
                    .stream()
                    .map(productMapper::toResponseDto)
                    .toList();

        } catch (CsvFileException
                | DuplicateProductException
                | CategoryNotFoundException ex) {

            throw ex;

        } catch (Exception ex) {

            throw new CsvFileException(
                    "Unable to read CSV file",
                    ex);
        }
    }

    private void validateFile(
            MultipartFile file) {

        if (file == null
                || file.isEmpty()) {

            throw new CsvFileException(
                    "CSV file is required");
        }

        String fileName =
                file.getOriginalFilename();

        if (fileName == null
                || !fileName
                        .toLowerCase(Locale.ROOT)
                        .endsWith(".csv")) {

            throw new CsvFileException(
                    "Only CSV files are allowed");
        }
    }

    private void validateHeader(
            String header) {

        if (header == null) {

            throw new CsvFileException(
                    "CSV file is empty");
        }

        String expectedHeader =
                "name,description,price,quantity,categoryId";

        if (!header.trim()
                .equalsIgnoreCase(
                        expectedHeader)) {

            throw new CsvFileException(
                    "Invalid CSV header. Expected: "
                            + expectedHeader);
        }
    }

    private String[] parseCsvLine(
            String line) {

        List<String> values =
                new ArrayList<>();

        StringBuilder current =
                new StringBuilder();

        boolean insideQuotes = false;

        for (int i = 0;
             i < line.length();
             i++) {

            char character =
                    line.charAt(i);

            if (character == '"') {

                if (insideQuotes
                        && i + 1 < line.length()
                        && line.charAt(i + 1) == '"') {

                    current.append('"');

                    i++;

                } else {

                    insideQuotes =
                            !insideQuotes;
                }

            } else if (character == ','
                    && !insideQuotes) {

                values.add(
                        current.toString());

                current.setLength(0);

            } else {

                current.append(character);
            }
        }

        if (insideQuotes) {

            throw new CsvFileException(
                    "Invalid CSV format: unmatched quotes");
        }

        values.add(
                current.toString());

        return values.toArray(
                new String[0]);
    }

    @Override
    public ProductResponseDto uploadProductImage(
            Long id,
            MultipartFile file) {

        Product product =
                productRepository.findById(id)
                        .orElseThrow(() ->
                                new ProductNotFoundException(
                                        "Product not found with id: "
                                                + id));

        if (file == null
                || file.isEmpty()) {

            throw new IllegalArgumentException(
                    "Image file is required");
        }

        String originalFileName =
                file.getOriginalFilename();

        String extension = "";

        if (originalFileName != null
                && originalFileName.contains(".")) {

            extension =
                    originalFileName.substring(
                            originalFileName.lastIndexOf("."));
        }

        String fileName =
                UUID.randomUUID()
                        + extension;

        Path uploadDirectory =
                Paths.get("uploads/products");

        try {

            Files.createDirectories(
                    uploadDirectory);

            Path filePath =
                    uploadDirectory.resolve(
                            fileName);

            Files.copy(
                    file.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException exception) {

            throw new RuntimeException(
                    "Failed to upload product image",
                    exception);
        }

        product.setImageUrl(
                "/images/products/"
                        + fileName);

        Product savedProduct =
                productRepository.save(product);

        return productMapper.toResponseDto(
                savedProduct);
    }

    @Override
    public Page<ProductResponseDto> filterProducts(
            String name,
            Long categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean inStock,
            Pageable pageable) {

        Page<Product> products =
                productRepository.findAll(
                        ProductSpecification.filterProducts(
                                name,
                                categoryId,
                                minPrice,
                                maxPrice,
                                inStock),
                        pageable);

        return products.map(
                productMapper::toResponseDto);
    }
}