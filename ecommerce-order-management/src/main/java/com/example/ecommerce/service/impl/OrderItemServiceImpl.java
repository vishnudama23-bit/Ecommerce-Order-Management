package com.example.ecommerce.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ecommerce.dto.request.OrderItemRequestDto;
import com.example.ecommerce.dto.response.OrderItemResponseDto;
import com.example.ecommerce.entity.Customer;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.OrderItem;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.enums.OrderStatus;
import com.example.ecommerce.exception.InsufficientStockException;
import com.example.ecommerce.exception.OrderItemNotFoundException;
import com.example.ecommerce.exception.OrderNotFoundException;
import com.example.ecommerce.exception.ProductNotFoundException;
import com.example.ecommerce.mapper.OrderItemMapper;
import com.example.ecommerce.repository.OrderItemRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.OrderItemService;

@Service
public class OrderItemServiceImpl
        implements OrderItemService {

    private final OrderItemRepository orderItemRepository;

    private final OrderRepository orderRepository;

    private final ProductRepository productRepository;

    private final OrderItemMapper orderItemMapper;

    public OrderItemServiceImpl(
            OrderItemRepository orderItemRepository,
            OrderRepository orderRepository,
            ProductRepository productRepository,
            OrderItemMapper orderItemMapper) {

        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.orderItemMapper = orderItemMapper;
    }

    @Override
    @Transactional
    public OrderItemResponseDto createOrderItem(
            Long orderId,
            OrderItemRequestDto request) {

        Order order =
                orderRepository.findById(orderId)
                        .orElseThrow(() ->
                                new OrderNotFoundException(
                                        "Order not found with id: "
                                                + orderId));

        validateOrderOwnership(order);

        Product product =
                productRepository.findById(
                        request.getProductId())
                        .orElseThrow(() ->
                                new ProductNotFoundException(
                                        "Product not found with id: "
                                                + request.getProductId()));

        if (request.getQuantity() <= 0) {

            throw new IllegalArgumentException(
                    "Quantity must be greater than 0");
        }

        if (request.getQuantity()
                > product.getQuantity()) {

            throw new InsufficientStockException(
                    "Insufficient product stock. Available stock: "
                            + product.getQuantity());
        }

        product.setQuantity(
                product.getQuantity()
                        - request.getQuantity());

        productRepository.save(product);

        OrderItem orderItem =
                orderItemMapper.toEntity(request);

        orderItem.setOrder(order);

        orderItem.setProduct(product);

        orderItem.setPrice(product.getPrice());

        OrderItem savedOrderItem =
                orderItemRepository.save(orderItem);

        BigDecimal itemTotal =
                product.getPrice()
                        .multiply(
                                BigDecimal.valueOf(
                                        request.getQuantity()));

        BigDecimal currentOrderTotal =
                order.getTotalAmount();

        if (currentOrderTotal == null) {

            currentOrderTotal =
                    BigDecimal.ZERO;
        }

        BigDecimal newOrderTotal =
                currentOrderTotal.add(itemTotal);

        order.setTotalAmount(
                newOrderTotal);

        orderRepository.save(order);

        return orderItemMapper.toResponseDto(
                savedOrderItem);
    }

    @Override
    public List<OrderItemResponseDto> getAllOrderItems() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        List<OrderItem> orderItems;

        if (isAdmin(authentication)) {

            orderItems =
                    orderItemRepository.findAll();

        } else {

            orderItems =
                    orderItemRepository
                            .findByOrderCustomerUserEmail(
                                    authentication.getName());
        }

        return orderItems.stream()
                .map(orderItemMapper::toResponseDto)
                .toList();
    }

    @Override
    public OrderItemResponseDto getOrderItemById(
            Long id) {

        OrderItem orderItem =
                orderItemRepository.findById(id)
                        .orElseThrow(() ->
                                new OrderItemNotFoundException(
                                        "Order item not found with id: "
                                                + id));

        validateOrderItemOwnership(orderItem);

        return orderItemMapper.toResponseDto(
                orderItem);
    }

    @Override
    @Transactional
    public void deleteOrderItem(Long id) {

        OrderItem orderItem =
                orderItemRepository.findById(id)
                        .orElseThrow(() ->
                                new OrderItemNotFoundException(
                                        "Order item not found with id: "
                                                + id));

        validateOrderItemOwnership(orderItem);

        Order order =
                orderItem.getOrder();

        if (order.getStatus()
                != OrderStatus.CANCELLED) {

            Product product =
                    orderItem.getProduct();

            product.setQuantity(
                    product.getQuantity()
                            + orderItem.getQuantity());

            productRepository.save(product);
        }

        BigDecimal itemTotal =
                orderItem.getPrice()
                        .multiply(
                                BigDecimal.valueOf(
                                        orderItem.getQuantity()));

        BigDecimal currentOrderTotal =
                order.getTotalAmount();

        if (currentOrderTotal == null) {

            currentOrderTotal =
                    BigDecimal.ZERO;
        }

        BigDecimal newOrderTotal =
                currentOrderTotal.subtract(
                        itemTotal);

        if (newOrderTotal.compareTo(
                BigDecimal.ZERO) < 0) {

            newOrderTotal =
                    BigDecimal.ZERO;
        }

        order.setTotalAmount(
                newOrderTotal);

        orderRepository.save(order);

        orderItemRepository.deleteById(id);
    }

    private void validateOrderOwnership(
            Order order) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (isAdmin(authentication)) {
            return;
        }

        if (order.getCustomer() == null) {

            throw new OrderNotFoundException(
                    "Order does not have a customer");
        }

        Customer customer =
                order.getCustomer();

        if (customer.getUser() == null) {

            throw new OrderNotFoundException(
                    "Order customer is not associated with a user");
        }

        if (!customer.getUser()
                .getEmail()
                .equals(authentication.getName())) {

            throw new OrderNotFoundException(
                    "You are not allowed to access this order");
        }
    }

    private void validateOrderItemOwnership(
            OrderItem orderItem) {

        if (orderItem.getOrder() == null) {

            throw new OrderNotFoundException(
                    "Order item is not associated with an order");
        }

        validateOrderOwnership(
                orderItem.getOrder());
    }

    private boolean isAdmin(
            Authentication authentication) {

        return authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority()
                                .equals("ROLE_ADMIN"));
    }
}