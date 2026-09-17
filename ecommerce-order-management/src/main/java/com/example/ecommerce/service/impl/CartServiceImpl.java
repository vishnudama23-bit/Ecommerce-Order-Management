package com.example.ecommerce.service.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.ecommerce.dto.request.CartItemRequestDto;
import com.example.ecommerce.dto.response.CartItemResponseDto;
import com.example.ecommerce.dto.response.CartResponseDto;
import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.entity.Customer;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.exception.CsvFileException;
import com.example.ecommerce.exception.ProductNotFoundException;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.CartService;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    private final CartItemRepository cartItemRepository;

    private final CustomerRepository customerRepository;

    private final ProductRepository productRepository;

    public CartServiceImpl(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            CustomerRepository customerRepository,
            ProductRepository productRepository) {

        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public CartResponseDto addItem(
            Long customerId,
            CartItemRequestDto request) {

        Customer customer =
                customerRepository.findById(customerId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Customer not found with id: "
                                                + customerId));

        validateCustomerOwnership(customer);

        Product product =
                productRepository.findById(
                        request.getProductId())
                        .orElseThrow(() ->
                                new ProductNotFoundException(
                                        "Product not found with id: "
                                                + request.getProductId()));

        Cart cart =
                cartRepository.findByCustomerId(
                        customerId)
                        .orElseGet(() -> {

                            Cart newCart =
                                    new Cart();

                            newCart.setCustomer(
                                    customer);

                            newCart.setCartItems(
                                    new ArrayList<>());

                            return cartRepository.save(
                                    newCart);
                        });

        CartItem cartItem =
                cartItemRepository
                        .findByCartIdAndProductId(
                                cart.getId(),
                                product.getId())
                        .orElse(null);

        if (cartItem == null) {

            if (request.getQuantity()
                    > product.getQuantity()) {

                throw new IllegalArgumentException(
                        "Insufficient stock for product: "
                                + product.getName());
            }

            cartItem =
                    new CartItem();

            cartItem.setCart(cart);

            cartItem.setProduct(product);

            cartItem.setQuantity(
                    request.getQuantity());

            cartItem.setPrice(
                    product.getPrice());

        } else {

            int newQuantity =
                    cartItem.getQuantity()
                            + request.getQuantity();

            if (newQuantity
                    > product.getQuantity()) {

                throw new IllegalArgumentException(
                        "Insufficient stock for product: "
                                + product.getName());
            }

            cartItem.setQuantity(
                    newQuantity);
        }

        cartItemRepository.save(
                cartItem);

        return buildCartResponse(
                cart);
    }

    @Override
    @Transactional
    public CartResponseDto addItems(
            Long customerId,
            List<CartItemRequestDto> requests) {

        if (requests == null
                || requests.isEmpty()) {

            throw new IllegalArgumentException(
                    "At least one cart item is required");
        }

        validateCustomerOwnershipById(
                customerId);

        for (CartItemRequestDto request
                : requests) {

            addItem(
                    customerId,
                    request);
        }

        return getCart(
                customerId);
    }

    @Override
    @Transactional
    public CartResponseDto addItemsFromFile(
            Long customerId,
            MultipartFile file) {

        validateCustomerOwnershipById(
                customerId);

        if (file == null
                || file.isEmpty()) {

            throw new CsvFileException(
                    "CSV file is required");
        }

        String fileName =
                file.getOriginalFilename();

        if (fileName == null
                || !fileName
                        .toLowerCase()
                        .endsWith(".csv")) {

            throw new CsvFileException(
                    "Only CSV files are supported");
        }

        List<CartItemRequestDto> requests =
                new ArrayList<>();

        try (
                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        file.getInputStream(),
                                        StandardCharsets.UTF_8))
        ) {

            String header =
                    reader.readLine();

            if (header == null) {

                throw new CsvFileException(
                        "CSV file is empty");
            }

            String[] headers =
                    header.split(",");

            if (headers.length != 2
                    || !"productName"
                            .equalsIgnoreCase(
                                    headers[0].trim())
                    || !"quantity"
                            .equalsIgnoreCase(
                                    headers[1].trim())) {

                throw new CsvFileException(
                        "CSV header must be: productName,quantity");
            }

            String line;

            int rowNumber = 1;

            while ((line =
                    reader.readLine()) != null) {

                rowNumber++;

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] values =
                        line.split(",");

                if (values.length != 2) {

                    throw new CsvFileException(
                            "Invalid CSV row at line "
                                    + rowNumber
                                    + ": "
                                    + line);
                }

                String productName =
                        values[0].trim();

                String quantityValue =
                        values[1].trim();

                if (productName.isEmpty()) {

                    throw new CsvFileException(
                            "Product name cannot be empty at line "
                                    + rowNumber);
                }

                Integer quantity;

                try {

                    quantity =
                            Integer.parseInt(
                                    quantityValue);

                } catch (NumberFormatException ex) {

                    throw new CsvFileException(
                            "Invalid quantity at line "
                                    + rowNumber
                                    + " for product: "
                                    + productName);
                }

                if (quantity <= 0) {

                    throw new CsvFileException(
                            "Quantity must be greater than zero at line "
                                    + rowNumber
                                    + " for product: "
                                    + productName);
                }

                Product product =
                        productRepository
                                .findByNameIgnoreCase(
                                        productName)
                                .orElseThrow(() ->
                                        new ProductNotFoundException(
                                                "Product not found with name: "
                                                        + productName));

                CartItemRequestDto request =
                        new CartItemRequestDto();

                request.setProductId(
                        product.getId());

                request.setQuantity(
                        quantity);

                requests.add(
                        request);
            }

        } catch (CsvFileException ex) {

            throw ex;

        } catch (ProductNotFoundException ex) {

            throw ex;

        } catch (Exception ex) {

            throw new CsvFileException(
                    "Unable to read CSV file",
                    ex);
        }

        if (requests.isEmpty()) {

            throw new CsvFileException(
                    "CSV file does not contain any products");
        }

        return addItems(
                customerId,
                requests);
    }

    @Override
    @Transactional
    public CartResponseDto getCart(
            Long customerId) {

        validateCustomerOwnershipById(
                customerId);

        Cart cart =
                cartRepository.findByCustomerId(
                        customerId)
                        .orElseGet(() -> {

                            Customer customer =
                                    customerRepository
                                            .findById(
                                                    customerId)
                                            .orElseThrow(() ->
                                                    new IllegalArgumentException(
                                                            "Customer not found with id: "
                                                                    + customerId));

                            Cart newCart =
                                    new Cart();

                            newCart.setCustomer(
                                    customer);

                            newCart.setCartItems(
                                    new ArrayList<>());

                            return cartRepository.save(
                                    newCart);
                        });

        return buildCartResponse(
                cart);
    }

    @Override
    @Transactional
    public CartResponseDto updateItemQuantity(
            Long customerId,
            Long productId,
            Integer quantity) {

        validateCustomerOwnershipById(
                customerId);

        if (quantity == null
                || quantity <= 0) {

            throw new IllegalArgumentException(
                    "Quantity must be greater than zero");
        }

        Cart cart =
                cartRepository.findByCustomerId(
                        customerId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Cart not found for customer id: "
                                                + customerId));

        CartItem cartItem =
                cartItemRepository
                        .findByCartIdAndProductId(
                                cart.getId(),
                                productId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Product not found in cart"));

        Product product =
                cartItem.getProduct();

        if (quantity
                > product.getQuantity()) {

            throw new IllegalArgumentException(
                    "Insufficient stock for product: "
                            + product.getName());
        }

        cartItem.setQuantity(
                quantity);

        cartItemRepository.save(
                cartItem);

        return buildCartResponse(
                cart);
    }

    @Override
    @Transactional
    public void removeItem(
            Long customerId,
            Long productId) {

        validateCustomerOwnershipById(
                customerId);

        Cart cart =
                cartRepository.findByCustomerId(
                        customerId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Cart not found for customer id: "
                                                + customerId));

        CartItem cartItem =
                cartItemRepository
                        .findByCartIdAndProductId(
                                cart.getId(),
                                productId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Product not found in cart"));

        cartItemRepository.delete(
                cartItem);
    }

    @Override
    @Transactional
    public void clearCart(
            Long customerId) {

        validateCustomerOwnershipById(
                customerId);

        Cart cart =
                cartRepository.findByCustomerId(
                        customerId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Cart not found for customer id: "
                                                + customerId));

        List<CartItem> cartItems =
                cartItemRepository.findByCartId(
                        cart.getId());

        cartItemRepository.deleteAll(
                cartItems);
    }

    private void validateCustomerOwnershipById(
            Long customerId) {

        Customer customer =
                customerRepository.findById(
                        customerId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Customer not found with id: "
                                                + customerId));

        validateCustomerOwnership(
                customer);
    }

    private void validateCustomerOwnership(
            Customer customer) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (isAdmin(authentication)) {
            return;
        }

        String email =
                authentication.getName();

        if (customer.getUser() == null
                || customer.getUser().getEmail() == null
                || !customer.getUser()
                        .getEmail()
                        .equalsIgnoreCase(email)) {

            throw new IllegalArgumentException(
                    "You are not allowed to access this customer's cart");
        }
    }

    private boolean isAdmin(
            Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()) {

            return false;
        }

        return authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority()
                                .equals("ROLE_ADMIN"));
    }

    private CartResponseDto buildCartResponse(
            Cart cart) {

        List<CartItem> cartItems =
                cartItemRepository.findByCartId(
                        cart.getId());

        List<CartItemResponseDto> itemResponses =
                new ArrayList<>();

        BigDecimal totalAmount =
                BigDecimal.ZERO;

        for (CartItem cartItem
                : cartItems) {

            BigDecimal price =
                    cartItem.getPrice() != null
                            ? cartItem.getPrice()
                            : BigDecimal.ZERO;

            BigDecimal subtotal =
                    price.multiply(
                            BigDecimal.valueOf(
                                    cartItem.getQuantity()));

            CartItemResponseDto itemResponse =
                    new CartItemResponseDto();

            itemResponse.setId(
                    cartItem.getId());

            itemResponse.setProductId(
                    cartItem.getProduct()
                            .getId());

            itemResponse.setProductName(
                    cartItem.getProduct()
                            .getName());

            itemResponse.setPrice(
                    price);

            itemResponse.setQuantity(
                    cartItem.getQuantity());

            itemResponse.setSubtotal(
                    subtotal);

            itemResponses.add(
                    itemResponse);

            totalAmount =
                    totalAmount.add(
                            subtotal);
        }

        CartResponseDto response =
                new CartResponseDto();

        response.setId(
                cart.getId());

        response.setCustomerId(
                cart.getCustomer()
                        .getId());

        response.setCartItems(
                itemResponses);

        response.setTotalAmount(
                totalAmount);

        return response;
    }
}