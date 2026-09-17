package com.example.ecommerce.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.ecommerce.dto.request.CartItemRequestDto;
import com.example.ecommerce.dto.response.CartResponseDto;

public interface CartService {

    CartResponseDto addItem(
            Long customerId,
            CartItemRequestDto request);

    CartResponseDto addItems(
            Long customerId,
            List<CartItemRequestDto> requests);

    CartResponseDto addItemsFromFile(
            Long customerId,
            MultipartFile file);

    CartResponseDto getCart(
            Long customerId);

    CartResponseDto updateItemQuantity(
            Long customerId,
            Long productId,
            Integer quantity);

    void removeItem(
            Long customerId,
            Long productId);

    void clearCart(
            Long customerId);
}