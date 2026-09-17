package com.example.ecommerce.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ecommerce.dto.request.WishlistRequestDto;
import com.example.ecommerce.dto.response.WishlistResponseDto;
import com.example.ecommerce.entity.Customer;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.Wishlist;
import com.example.ecommerce.exception.CustomerNotFoundException;
import com.example.ecommerce.exception.ProductNotFoundException;
import com.example.ecommerce.mapper.WishlistMapper;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.WishlistRepository;
import com.example.ecommerce.service.WishlistService;

@Service
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final WishlistMapper wishlistMapper;

    public WishlistServiceImpl(
            WishlistRepository wishlistRepository,
            CustomerRepository customerRepository,
            ProductRepository productRepository,
            WishlistMapper wishlistMapper) {

        this.wishlistRepository = wishlistRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.wishlistMapper = wishlistMapper;
    }

    @Override
    @Transactional
    public WishlistResponseDto addToWishlist(
            WishlistRequestDto request) {

        Authentication authentication =
                org.springframework.security.core.context.SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        Customer customer =
                customerRepository.findById(
                        request.getCustomerId())
                        .orElseThrow(() ->
                                new CustomerNotFoundException(
                                        "Customer not found with id: "
                                                + request.getCustomerId()));

        validateCustomerOwnership(
                customer,
                authentication);

        Product product =
                productRepository.findById(
                        request.getProductId())
                        .orElseThrow(() ->
                                new ProductNotFoundException(
                                        "Product not found with id: "
                                                + request.getProductId()));

        boolean alreadyExists =
                wishlistRepository
                        .existsByCustomerIdAndProductId(
                                request.getCustomerId(),
                                request.getProductId());

        if (alreadyExists) {
            throw new RuntimeException(
                    "Product is already in the wishlist");
        }

        Wishlist wishlist =
                wishlistMapper.toEntity(request);

        wishlist.setCustomer(customer);
        wishlist.setProduct(product);
        wishlist.setAddedDate(LocalDateTime.now());

        Wishlist savedWishlist =
                wishlistRepository.save(wishlist);

        return wishlistMapper.toResponseDto(
                savedWishlist);
    }

    @Override
    public List<WishlistResponseDto> getWishlist(
            Long customerId) {

        Authentication authentication =
                org.springframework.security.core.context.SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        Customer customer =
                customerRepository.findById(customerId)
                        .orElseThrow(() ->
                                new CustomerNotFoundException(
                                        "Customer not found with id: "
                                                + customerId));

        validateCustomerOwnership(
                customer,
                authentication);

        List<Wishlist> wishlistItems =
                wishlistRepository.findByCustomerId(
                        customerId);

        return wishlistItems.stream()
                .map(wishlistMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public void removeFromWishlist(
            Long customerId,
            Long productId) {

        Authentication authentication =
                org.springframework.security.core.context.SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        Customer customer =
                customerRepository.findById(customerId)
                        .orElseThrow(() ->
                                new CustomerNotFoundException(
                                        "Customer not found with id: "
                                                + customerId));

        validateCustomerOwnership(
                customer,
                authentication);

        Wishlist wishlist =
                wishlistRepository
                        .findByCustomerIdAndProductId(
                                customerId,
                                productId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Product is not in the wishlist"));

        wishlistRepository.delete(wishlist);
    }

    @Override
    @Transactional
    public void clearWishlist(
            Long customerId) {

        Authentication authentication =
                org.springframework.security.core.context.SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        Customer customer =
                customerRepository.findById(customerId)
                        .orElseThrow(() ->
                                new CustomerNotFoundException(
                                        "Customer not found with id: "
                                                + customerId));

        validateCustomerOwnership(
                customer,
                authentication);

        List<Wishlist> wishlistItems =
                wishlistRepository.findByCustomerId(
                        customerId);

        wishlistRepository.deleteAll(
                wishlistItems);
    }

    private void validateCustomerOwnership(
            Customer customer,
            Authentication authentication) {

        boolean isAdmin =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_ADMIN"));

        if (isAdmin) {
            return;
        }

        String loggedInEmail =
                authentication.getName();

        if (customer.getUser() == null) {
            throw new RuntimeException(
                    "Customer is not linked to a user account");
        }

        String customerUserEmail =
                customer.getUser().getEmail();

        if (!loggedInEmail.equalsIgnoreCase(
                customerUserEmail)) {

            throw new RuntimeException(
                    "You are not authorized to access this customer's wishlist");
        }
    }
}