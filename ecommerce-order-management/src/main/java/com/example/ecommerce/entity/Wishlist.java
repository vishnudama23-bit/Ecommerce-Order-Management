package com.example.ecommerce.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "wishlists",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"customer_id", "product_id"}
        )
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime addedDate;

    @ManyToOne
    @JoinColumn(
            name = "customer_id",
            nullable = false
    )
    private Customer customer;

    @ManyToOne
    @JoinColumn(
            name = "product_id",
            nullable = false
    )
    private Product product;
}