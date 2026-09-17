package com.example.ecommerce.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecommerce.dto.request.WishlistRequestDto;
import com.example.ecommerce.dto.response.WishlistResponseDto;
import com.example.ecommerce.service.WishlistService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/wishlists")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(
            WishlistService wishlistService) {

        this.wishlistService = wishlistService;
    }

    @PostMapping
    public ResponseEntity<WishlistResponseDto> addToWishlist(
            @Valid @RequestBody WishlistRequestDto request) {

        WishlistResponseDto response =
                wishlistService.addToWishlist(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<List<WishlistResponseDto>> getWishlist(
            @PathVariable Long customerId) {

        List<WishlistResponseDto> response =
                wishlistService.getWishlist(customerId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{customerId}/product/{productId}")
    public ResponseEntity<Void> removeFromWishlist(
            @PathVariable Long customerId,
            @PathVariable Long productId) {

        wishlistService.removeFromWishlist(
                customerId,
                productId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{customerId}/clear")
    public ResponseEntity<Void> clearWishlist(
            @PathVariable Long customerId) {

        wishlistService.clearWishlist(customerId);

        return ResponseEntity.noContent().build();
    }
}