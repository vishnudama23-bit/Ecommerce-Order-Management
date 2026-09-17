package com.example.ecommerce.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ecommerce.dto.request.CategoryRequestDto;
import com.example.ecommerce.dto.response.CategoryResponseDto;
import com.example.ecommerce.entity.Category;
import com.example.ecommerce.exception.CategoryInUseException;
import com.example.ecommerce.exception.CategoryNotFoundException;
import com.example.ecommerce.mapper.CategoryMapper;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.CategoryService;

@Service
public class CategoryServiceImpl
        implements CategoryService {

    private static final Logger logger =
            LoggerFactory.getLogger(
                    CategoryServiceImpl.class);

    private final CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper;

    private final ProductRepository productRepository;

    public CategoryServiceImpl(
            CategoryRepository categoryRepository,
            CategoryMapper categoryMapper,
            ProductRepository productRepository) {

        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public CategoryResponseDto createOrUpdateCategory(
            Long id,
            CategoryRequestDto request) {

        logger.info(
                "Creating or updating category with id: {}",
                id);

        Category category;

        if (id == null) {

            logger.info(
                    "Creating new category with name: {}",
                    request.getName());

            category =
                    categoryMapper.toEntity(request);

        } else {

            logger.info(
                    "Updating existing category with id: {}",
                    id);

            category =
                    categoryRepository.findById(id)
                            .orElseThrow(() -> {

                                logger.warn(
                                        "Category not found with id: {}",
                                        id);

                                return new CategoryNotFoundException(
                                        "Category not found with id: "
                                                + id);
                            });

            category.setName(
                    request.getName());
        }

        Category savedCategory =
                categoryRepository.save(category);

        logger.info(
                "Category saved successfully with id: {}",
                savedCategory.getId());

        return categoryMapper.toResponseDto(
                savedCategory);
    }

    @Override
    public List<CategoryResponseDto>
            getAllCategories() {

        logger.info(
                "Fetching all categories");

        List<Category> categories =
                categoryRepository.findAll();

        logger.info(
                "Fetched {} categories",
                categories.size());

        return categories.stream()
                .map(categoryMapper::toResponseDto)
                .toList();
    }

    @Override
    public CategoryResponseDto getCategoryById(
            Long id) {

        logger.info(
                "Fetching category with id: {}",
                id);

        Category category =
                categoryRepository.findById(id)
                        .orElseThrow(() -> {

                            logger.warn(
                                    "Category not found with id: {}",
                                    id);

                            return new CategoryNotFoundException(
                                    "Category not found with id: "
                                            + id);
                        });

        logger.info(
                "Category found successfully with id: {}",
                id);

        return categoryMapper.toResponseDto(
                category);
    }

    @Override
    @Transactional
    public void deleteCategory(
            Long id) {

        logger.info(
                "Deleting category with id: {}",
                id);

        if (!categoryRepository.existsById(id)) {

            logger.warn(
                    "Category not found with id: {}",
                    id);

            throw new CategoryNotFoundException(
                    "Category not found with id: "
                            + id);
        }

        if (productRepository.existsByCategoryId(id)) {

            logger.warn(
                    "Cannot delete category with id: {} because products are using it",
                    id);

            throw new CategoryInUseException(
                    "Cannot delete category because products are using it");
        }

        categoryRepository.deleteById(id);

        logger.info(
                "Category deleted successfully with id: {}",
                id);
    }
}