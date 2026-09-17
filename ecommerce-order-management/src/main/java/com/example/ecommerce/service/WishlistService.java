package com.example.ecommerce.service;

import java.util.List;

import com.example.ecommerce.dto.request.WishlistRequestDto;
import com.example.ecommerce.dto.response.WishlistResponseDto;

public interface WishlistService {

    WishlistResponseDto addToWishlist(
            WishlistRequestDto request);

    List<WishlistResponseDto> getWishlist(
            Long customerId);

    void removeFromWishlist(
            Long customerId,
            Long productId);

    void clearWishlist(
            Long customerId);
}
