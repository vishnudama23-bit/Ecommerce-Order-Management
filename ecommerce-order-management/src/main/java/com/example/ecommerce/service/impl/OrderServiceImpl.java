package com.example.ecommerce.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ecommerce.dto.request.OrderItemRequestDto;
import com.example.ecommerce.dto.request.OrderRequestDto;
import com.example.ecommerce.dto.response.OrderResponseDto;
import com.example.ecommerce.entity.Customer;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.OrderItem;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.enums.OrderStatus;
import com.example.ecommerce.exception.CustomerNotFoundException;
import com.example.ecommerce.exception.InsufficientStockException;
import com.example.ecommerce.exception.InvalidOrderException;
import com.example.ecommerce.exception.InvalidOrderStatusException;
import com.example.ecommerce.exception.OrderItemNotFoundException;
import com.example.ecommerce.exception.OrderNotFoundException;
import com.example.ecommerce.exception.ProductNotFoundException;
import com.example.ecommerce.mapper.OrderMapper;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.repository.OrderItemRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;

    private final CustomerRepository customerRepository;

    private final ProductRepository productRepository;

    private final OrderMapper orderMapper;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            CustomerRepository customerRepository,
            ProductRepository productRepository,
            OrderMapper orderMapper) {

        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    @Transactional
    public OrderResponseDto createOrUpdateOrder(
            Long id,
            OrderRequestDto request) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        boolean isAdmin =
                isAdmin(authentication);

        Customer customer =
                customerRepository.findById(
                        request.getCustomerId())
                        .orElseThrow(() ->
                                new InvalidOrderException(
                                        "Customer not found with id: "
                                                + request.getCustomerId()));

        validateCustomerOwnership(customer);

        Order order;

        if (id == null) {

            order =
                    orderMapper.toEntity(request);

            order.setCustomer(customer);

            if (!isAdmin) {

                order.setStatus(
                        OrderStatus.PLACED);

            } else if (order.getStatus() == null) {

                order.setStatus(
                        OrderStatus.PLACED);
            }

            if (order.getOrderDate() == null) {

                order.setOrderDate(
                        LocalDateTime.now());
            }

            if (order.getTotalAmount() == null) {

                order.setTotalAmount(
                        BigDecimal.ZERO);
            }

        } else {

            order =
                    orderRepository.findById(id)
                            .orElseThrow(() ->
                                    new OrderNotFoundException(
                                            "Order not found with id: "
                                                    + id));

            validateOrderOwnership(order);

            if (request.getCustomerId() != null) {

                order.setCustomer(customer);
            }

            if (request.getStatus() != null) {

                validateStatusTransition(
                        order.getStatus(),
                        request.getStatus());

                order.setStatus(
                        request.getStatus());
            }
        }

        Order savedOrder =
                orderRepository.save(order);

        return orderMapper.toResponseDto(
                savedOrder);
    }

    @Override
    public OrderResponseDto getOrderById(
            Long id) {

        Order order =
                orderRepository.findById(id)
                        .orElseThrow(() ->
                                new OrderNotFoundException(
                                        "Order not found with id: "
                                                + id));

        validateOrderOwnership(order);

        return orderMapper.toResponseDto(
                order);
    }

    @Override
    public List<OrderResponseDto> getAllOrders() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        List<Order> orders;

        if (isAdmin(authentication)) {

            orders =
                    orderRepository.findAll();

        } else {

            String email =
                    authentication.getName();

            orders =
                    orderRepository
                            .findAll()
                            .stream()
                            .filter(order ->
                                    order.getCustomer() != null &&
                                    order.getCustomer().getUser() != null &&
                                    order.getCustomer()
                                            .getUser()
                                            .getEmail()
                                            .equalsIgnoreCase(email))
                            .toList();
        }

        return orders.stream()
                .map(orderMapper::toResponseDto)
                .toList();
    }

    @Override
    public Page<OrderResponseDto> getAllOrders(
            Pageable pageable) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (isAdmin(authentication)) {

            return orderRepository
                    .findAll(pageable)
                    .map(orderMapper::toResponseDto);
        }

        String email =
                authentication.getName();

        List<Order> orders =
                orderRepository
                        .findAll()
                        .stream()
                        .filter(order ->
                                order.getCustomer() != null &&
                                order.getCustomer().getUser() != null &&
                                order.getCustomer()
                                        .getUser()
                                        .getEmail()
                                        .equalsIgnoreCase(email))
                        .toList();

        int start =
                (int) pageable.getOffset();

        int end =
                Math.min(
                        start + pageable.getPageSize(),
                        orders.size());

        List<Order> pageContent;

        if (start >= orders.size()) {

            pageContent =
                    List.of();

        } else {

            pageContent =
                    orders.subList(start, end);
        }

        Page<Order> orderPage =
                new PageImpl<>(
                        pageContent,
                        pageable,
                        orders.size());

        return orderPage.map(
                orderMapper::toResponseDto);
    }

    @Override
    public List<OrderResponseDto> getOrdersByCustomerId(
            Long customerId) {

        Customer customer =
                customerRepository.findById(customerId)
                        .orElseThrow(() ->
                                new InvalidOrderException(
                                        "Customer not found with id: "
                                                + customerId));

        validateCustomerOwnership(customer);

        List<Order> orders =
                orderRepository.findByCustomerId(
                        customerId);

        return orders.stream()
                .map(orderMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<OrderResponseDto> getOrdersByCustomer(
            Long customerId) {

        return getOrdersByCustomerId(
                customerId);
    }

    @Override
    public Page<OrderResponseDto> getOrdersByCustomer(
            Long customerId,
            Pageable pageable) {

        Customer customer =
                customerRepository.findById(customerId)
                        .orElseThrow(() ->
                                new InvalidOrderException(
                                        "Customer not found with id: "
                                                + customerId));

        validateCustomerOwnership(customer);

        return orderRepository
                .findByCustomerId(
                        customerId,
                        pageable)
                .map(orderMapper::toResponseDto);
    }

    @Override
    @Transactional
    public OrderResponseDto updateOrderStatus(
            Long id,
            OrderStatus newStatus) {

        Order order =
                orderRepository.findById(id)
                        .orElseThrow(() ->
                                new OrderNotFoundException(
                                        "Order not found with id: "
                                                + id));

        validateOrderOwnership(order);

        OrderStatus currentStatus =
                order.getStatus();

        if (currentStatus == newStatus) {

            return orderMapper.toResponseDto(
                    order);
        }

        validateStatusTransition(
                currentStatus,
                newStatus);

        if (newStatus == OrderStatus.CANCELLED &&
                currentStatus != OrderStatus.CANCELLED) {

            restoreStock(order);
        }

        order.setStatus(newStatus);

        Order savedOrder =
                orderRepository.save(order);

        return orderMapper.toResponseDto(
                savedOrder);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {

        Order order =
                orderRepository.findById(id)
                        .orElseThrow(() ->
                                new OrderNotFoundException(
                                        "Order not found with id: "
                                                + id));

        validateOrderOwnership(order);

        if (order.getStatus()
                != OrderStatus.PLACED) {

            throw new InvalidOrderException(
                    "Only PLACED orders can be deleted");
        }

        orderRepository.delete(order);
    }

    @Override
    @Transactional
    public void cancelOrder(Long id) {

        Order order =
                orderRepository.findById(id)
                        .orElseThrow(() ->
                                new OrderNotFoundException(
                                        "Order not found with id: "
                                                + id));

        validateOrderOwnership(order);

        if (order.getStatus()
                != OrderStatus.PLACED) {

            throw new InvalidOrderStatusException(
                    "Only PLACED orders can be cancelled");
        }

        restoreStock(order);

        order.setStatus(
                OrderStatus.CANCELLED);

        orderRepository.save(order);
    }

    @Override
    @Transactional
    public OrderResponseDto addOrderItem(
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
                new OrderItem();

        orderItem.setOrder(order);

        orderItem.setProduct(product);

        orderItem.setQuantity(
                request.getQuantity());

        orderItem.setPrice(
                product.getPrice());

        orderItemRepository.save(orderItem);

        BigDecimal itemTotal =
                product.getPrice()
                        .multiply(
                                BigDecimal.valueOf(
                                        request.getQuantity()));

        BigDecimal currentTotal =
                order.getTotalAmount();

        if (currentTotal == null) {

            currentTotal =
                    BigDecimal.ZERO;
        }

        order.setTotalAmount(
                currentTotal.add(itemTotal));

        Order savedOrder =
                orderRepository.save(order);

        return orderMapper.toResponseDto(
                savedOrder);
    }

    @Override
    @Transactional
    public void removeOrderItem(
            Long orderId,
            Long orderItemId) {

        Order order =
                orderRepository.findById(orderId)
                        .orElseThrow(() ->
                                new OrderNotFoundException(
                                        "Order not found with id: "
                                                + orderId));

        validateOrderOwnership(order);

        OrderItem orderItem =
                orderItemRepository.findById(
                        orderItemId)
                        .orElseThrow(() ->
                                new OrderItemNotFoundException(
                                        "Order item not found with id: "
                                                + orderItemId));

        if (orderItem.getOrder() == null ||
                !orderItem.getOrder()
                        .getId()
                        .equals(orderId)) {

            throw new OrderItemNotFoundException(
                    "Order item not found for order id: "
                            + orderId);
        }

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

        BigDecimal currentTotal =
                order.getTotalAmount();

        if (currentTotal == null) {

            currentTotal =
                    BigDecimal.ZERO;
        }

        BigDecimal newTotal =
                currentTotal.subtract(
                        itemTotal);

        if (newTotal.compareTo(
                BigDecimal.ZERO) < 0) {

            newTotal =
                    BigDecimal.ZERO;
        }

        order.setTotalAmount(
                newTotal);

        orderRepository.save(order);

        orderItemRepository.delete(orderItem);
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

        if (customer.getUser() == null) {

            throw new InvalidOrderException(
                    "Customer is not associated with a user");
        }

        if (!customer.getUser()
                .getEmail()
                .equalsIgnoreCase(
                        authentication.getName())) {

            throw new InvalidOrderException(
                    "You are not allowed to access this customer");
        }
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
                .equalsIgnoreCase(
                        authentication.getName())) {

            throw new OrderNotFoundException(
                    "Order not found with id: "
                            + order.getId());
        }
    }

    private boolean isAdmin(
            Authentication authentication) {

        return authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority()
                                .equals("ROLE_ADMIN"));
    }

    private void validateStatusTransition(
            OrderStatus currentStatus,
            OrderStatus newStatus) {

        if (currentStatus == newStatus) {
            return;
        }

        if (currentStatus == OrderStatus.PLACED &&
                (newStatus == OrderStatus.PAID ||
                 newStatus == OrderStatus.CANCELLED)) {

            return;
        }

        if (currentStatus == OrderStatus.PAID &&
                (newStatus == OrderStatus.CONFIRMED ||
                 newStatus == OrderStatus.CANCELLED)) {

            return;
        }

        if (currentStatus == OrderStatus.CONFIRMED &&
                (newStatus == OrderStatus.SHIPPED ||
                 newStatus == OrderStatus.CANCELLED)) {

            return;
        }

        if (currentStatus == OrderStatus.SHIPPED &&
                newStatus == OrderStatus.DELIVERED) {

            return;
        }

        throw new InvalidOrderStatusException(
                "Invalid order status transition from "
                        + currentStatus
                        + " to "
                        + newStatus);
    }

    private void restoreStock(
            Order order) {

        List<OrderItem> orderItems =
                orderItemRepository.findByOrderId(
                        order.getId());

        for (OrderItem orderItem : orderItems) {

            Product product =
                    orderItem.getProduct();

            if (product != null) {

                product.setQuantity(
                        product.getQuantity()
                                + orderItem.getQuantity());

                productRepository.save(product);
            }
        }
    }
    
    @Override
    @Transactional
    public void cancelOrderAutomatically(Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new OrderNotFoundException(
                                "Order not found with id: " + id));

        if (order.getStatus() != OrderStatus.PLACED) {
            return;
        }

        List<OrderItem> orderItems =
                orderItemRepository.findByOrderId(id);

        for (OrderItem orderItem : orderItems) {

            Product product = orderItem.getProduct();

            product.setQuantity(
                    product.getQuantity()
                            + orderItem.getQuantity());

            productRepository.save(product);
        }

        order.setStatus(OrderStatus.CANCELLED);

        orderRepository.save(order);
    }@Override
    public List<OrderResponseDto> searchOrdersByStatus(OrderStatus status) {

        List<Order> orders =
                orderRepository.findByStatus(status);

        return orders.stream()
                .map(orderMapper::toResponseDto)
                .toList();
    }
}