package com.example.ecommerce.controller;

import java.util.List;

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

import com.example.ecommerce.dto.request.CartItemRequestDto;
import com.example.ecommerce.dto.response.CartResponseDto;
import com.example.ecommerce.service.CartService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/carts")
public class CartController {

    private final CartService cartService;

    public CartController(
            CartService cartService) {

        this.cartService = cartService;
    }

    @PostMapping("/{customerId}/items")
    public ResponseEntity<CartResponseDto> addItem(
            @PathVariable Long customerId,
            @Valid @RequestBody CartItemRequestDto request) {

        CartResponseDto response =
                cartService.addItem(
                        customerId,
                        request);

        return ResponseEntity.ok(
                response);
    }

    @PostMapping("/{customerId}/items/bulk")
    public ResponseEntity<CartResponseDto> addItems(
            @PathVariable Long customerId,
            @Valid @RequestBody
            List<CartItemRequestDto> requests) {

        CartResponseDto response =
                cartService.addItems(
                        customerId,
                        requests);

        return ResponseEntity.ok(
                response);
    }

    @PostMapping(
            value = "/{customerId}/items/file",
            consumes = "multipart/form-data")
    public ResponseEntity<CartResponseDto> addItemsFromFile(
            @PathVariable Long customerId,
            @RequestParam("file")
            MultipartFile file) {

        CartResponseDto response =
                cartService.addItemsFromFile(
                        customerId,
                        file);

        return ResponseEntity.ok(
                response);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CartResponseDto> getCart(
            @PathVariable Long customerId) {

        CartResponseDto response =
                cartService.getCart(
                        customerId);

        return ResponseEntity.ok(
                response);
    }

    @PutMapping("/{customerId}/items/{productId}")
    public ResponseEntity<CartResponseDto> updateItemQuantity(
            @PathVariable Long customerId,
            @PathVariable Long productId,
            @RequestParam Integer quantity) {

        CartResponseDto response =
                cartService.updateItemQuantity(
                        customerId,
                        productId,
                        quantity);

        return ResponseEntity.ok(
                response);
    }

    @DeleteMapping("/{customerId}/items/{productId}")
    public ResponseEntity<Void> removeItem(
            @PathVariable Long customerId,
            @PathVariable Long productId) {

        cartService.removeItem(
                customerId,
                productId);

        return ResponseEntity
                .noContent()
                .build();
    }

    @DeleteMapping("/{customerId}/items")
    public ResponseEntity<Void> clearCart(
            @PathVariable Long customerId) {

        cartService.clearCart(
                customerId);

        return ResponseEntity
                .noContent()
                .build();
    }
}