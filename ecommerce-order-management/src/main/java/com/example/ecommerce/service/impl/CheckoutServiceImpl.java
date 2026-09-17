package com.example.ecommerce.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ecommerce.dto.response.OrderResponseDto;
import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.entity.Customer;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.OrderItem;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.enums.OrderStatus;
import com.example.ecommerce.exception.CustomerNotFoundException;
import com.example.ecommerce.exception.InsufficientStockException;
import com.example.ecommerce.exception.InvalidOrderException;
import com.example.ecommerce.exception.OrderNotFoundException;
import com.example.ecommerce.mapper.OrderMapper;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.repository.OrderItemRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.CheckoutService;
import com.example.ecommerce.service.EmailService;

@Service
public class CheckoutServiceImpl implements CheckoutService {

    private final CustomerRepository customerRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;
    private final EmailService emailService;

    public CheckoutServiceImpl(
            CustomerRepository customerRepository,
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            ProductRepository productRepository,
            OrderMapper orderMapper,
            EmailService emailService) {

        this.customerRepository = customerRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.orderMapper = orderMapper;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public OrderResponseDto checkout(Long customerId) {

        Customer customer =
                customerRepository.findById(customerId)
                        .orElseThrow(() ->
                                new CustomerNotFoundException(
                                        "Customer not found with id: "
                                                + customerId));

        Cart cart =
                cartRepository.findByCustomerId(customerId)
                        .orElseThrow(() ->
                                new InvalidOrderException(
                                        "Cart not found for customer id: "
                                                + customerId));

        List<CartItem> cartItems =
                cartItemRepository.findByCartId(
                        cart.getId());

        if (cartItems.isEmpty()) {

            throw new InvalidOrderException(
                    "Cannot checkout because cart is empty");
        }

        for (CartItem cartItem : cartItems) {

            Product product =
                    cartItem.getProduct();

            if (cartItem.getQuantity() == null
                    || cartItem.getQuantity() <= 0) {

                throw new InvalidOrderException(
                        "Invalid quantity for product: "
                                + product.getName());
            }

            if (cartItem.getQuantity()
                    > product.getQuantity()) {

                throw new InsufficientStockException(
                        "Insufficient stock for product: "
                                + product.getName()
                                + ". Available stock: "
                                + product.getQuantity());
            }
        }

        Order order = new Order();

        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PLACED);
        order.setTotalAmount(BigDecimal.ZERO);
        order.setOrderItems(new ArrayList<>());

        Order savedOrder =
                orderRepository.save(order);

        BigDecimal totalAmount =
                BigDecimal.ZERO;

        List<OrderItem> orderItems =
                new ArrayList<>();

        for (CartItem cartItem : cartItems) {

            Product product =
                    cartItem.getProduct();

            Integer quantity =
                    cartItem.getQuantity();

            BigDecimal price =
                    product.getPrice();

            BigDecimal itemTotal =
                    price.multiply(
                            BigDecimal.valueOf(quantity));

            product.setQuantity(
                    product.getQuantity()
                            - quantity);

            productRepository.save(product);

            OrderItem orderItem =
                    new OrderItem();

            orderItem.setOrder(savedOrder);
            orderItem.setProduct(product);
            orderItem.setQuantity(quantity);
            orderItem.setPrice(price);

            orderItems.add(orderItem);

            totalAmount =
                    totalAmount.add(itemTotal);
        }

        orderItemRepository.saveAll(orderItems);

        savedOrder.setTotalAmount(totalAmount);
        savedOrder.setOrderItems(orderItems);

        Order finalOrder =
                orderRepository.save(savedOrder);

        cartItemRepository.deleteAll(cartItems);

        emailService.sendEmail(
                customer.getEmail(),
                "Order Confirmation",
                "Your order has been placed successfully. "
                        + "Order ID: "
                        + finalOrder.getId());

        return orderMapper.toResponseDto(
                finalOrder);
    }
}