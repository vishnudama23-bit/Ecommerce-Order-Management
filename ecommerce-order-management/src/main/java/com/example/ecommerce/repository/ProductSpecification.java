package com.example.ecommerce.repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.example.ecommerce.entity.Product;

import jakarta.persistence.criteria.Predicate;

public class ProductSpecification {

    private ProductSpecification() {
    }

    public static Specification<Product> filterProducts(
            String name,
            Long categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean inStock) {

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates =
                    new ArrayList<>();

            if (name != null && !name.isBlank()) {

                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        root.get("name")),
                                "%"
                                        + name.trim().toLowerCase()
                                        + "%"));
            }

            if (categoryId != null) {

                predicates.add(
                        criteriaBuilder.equal(
                                root.get("category")
                                        .get("id"),
                                categoryId));
            }

            if (minPrice != null) {

                predicates.add(
                        criteriaBuilder
                                .greaterThanOrEqualTo(
                                        root.get("price"),
                                        minPrice));
            }

            if (maxPrice != null) {

                predicates.add(
                        criteriaBuilder
                                .lessThanOrEqualTo(
                                        root.get("price"),
                                        maxPrice));
            }

            if (Boolean.TRUE.equals(inStock)) {

                predicates.add(
                        criteriaBuilder.greaterThan(
                                root.get("quantity"),
                                0));
            }

            return criteriaBuilder.and(
                    predicates.toArray(
                            new Predicate[0]));
        };
    }
}