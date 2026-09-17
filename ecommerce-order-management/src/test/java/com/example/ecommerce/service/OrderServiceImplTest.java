package com.example.ecommerce.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.ecommerce.dto.request.OrderRequestDto;
import com.example.ecommerce.dto.response.OrderResponseDto;
import com.example.ecommerce.entity.Customer;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.OrderItem;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.enums.OrderStatus;
import com.example.ecommerce.exception.CustomerNotFoundException;
import com.example.ecommerce.exception.InvalidOrderException;
import com.example.ecommerce.exception.InvalidOrderStatusException;
import com.example.ecommerce.exception.OrderNotFoundException;
import com.example.ecommerce.mapper.OrderMapper;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.repository.OrderItemRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.impl.OrderServiceImpl;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Customer customer;

    private Product product;

    private Order order;

    private OrderItem orderItem;

    private OrderResponseDto orderResponseDto;

    @BeforeEach
    void setUp() {

        customer = new Customer();

        customer.setId(1L);
        customer.setName("John");
        customer.setEmail("john@gmail.com");
        customer.setPhone("9876543210");

        product = new Product();

        product.setId(1L);
        product.setName("Laptop");
        product.setPrice(new BigDecimal("50000"));
        product.setQuantity(10);

        order = new Order();

        order.setId(1L);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PLACED);
        order.setTotalAmount(BigDecimal.ZERO);
        order.setCustomer(customer);

        orderItem = new OrderItem();

        orderItem.setId(1L);
        orderItem.setQuantity(2);
        orderItem.setPrice(new BigDecimal("50000"));
        orderItem.setProduct(product);
        orderItem.setOrder(order);

        orderResponseDto = new OrderResponseDto();

        orderResponseDto.setId(1L);
    }

    @Test
    void createOrder_shouldCreateOrderSuccessfully() {

        OrderRequestDto request =
                new OrderRequestDto();

        request.setCustomerId(1L);
        request.setStatus(OrderStatus.PLACED);

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        when(orderMapper.toEntity(request))
                .thenReturn(order);

        when(orderRepository.save(order))
                .thenReturn(order);

        when(orderMapper.toResponseDto(order))
                .thenReturn(orderResponseDto);

        OrderResponseDto result =
                orderService.createOrUpdateOrder(
                        null,
                        request);

        assertEquals(
                orderResponseDto,
                result);

        verify(customerRepository)
                .findById(1L);

        verify(orderRepository)
                .save(order);

        verify(emailService)
                .sendEmail(
                        customer.getEmail(),
                        "Order Confirmation",
                        "Your order has been placed successfully. "
                                + "Order ID: "
                                + order.getId());
    }

    @Test
    void createOrder_shouldThrowExceptionWhenCustomerNotFound() {

        OrderRequestDto request =
                new OrderRequestDto();

        request.setCustomerId(999L);
        request.setStatus(OrderStatus.PLACED);

        when(customerRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                CustomerNotFoundException.class,
                () -> orderService.createOrUpdateOrder(
                        null,
                        request));

        verify(orderRepository, never())
                .save(any(Order.class));

        verify(emailService, never())
                .sendEmail(
                        any(),
                        any(),
                        any());
    }

    @Test
    void updateOrder_shouldUpdateSuccessfully() {

        OrderRequestDto request =
                new OrderRequestDto();

        request.setCustomerId(1L);
        request.setStatus(OrderStatus.PAID);

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(orderRepository.save(order))
                .thenReturn(order);

        when(orderMapper.toResponseDto(order))
                .thenReturn(orderResponseDto);

        OrderResponseDto result =
                orderService.createOrUpdateOrder(
                        1L,
                        request);

        assertEquals(
                OrderStatus.PAID,
                order.getStatus());

        assertEquals(
                orderResponseDto,
                result);

        verify(orderRepository)
                .save(order);

        verify(emailService, never())
                .sendEmail(
                        any(),
                        any(),
                        any());
    }

    @Test
    void updateOrder_shouldThrowExceptionWhenOrderNotFound() {

        OrderRequestDto request =
                new OrderRequestDto();

        request.setCustomerId(1L);
        request.setStatus(OrderStatus.PAID);

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        when(orderRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                OrderNotFoundException.class,
                () -> orderService.createOrUpdateOrder(
                        999L,
                        request));

        verify(orderRepository, never())
                .save(any(Order.class));
    }

    @Test
    void updateOrder_shouldThrowExceptionForInvalidStatusTransition() {

        order.setStatus(OrderStatus.PLACED);

        OrderRequestDto request =
                new OrderRequestDto();

        request.setCustomerId(1L);
        request.setStatus(OrderStatus.SHIPPED);

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        assertThrows(
                InvalidOrderStatusException.class,
                () -> orderService.createOrUpdateOrder(
                        1L,
                        request));

        verify(orderRepository, never())
                .save(any(Order.class));
    }

    @Test
    void updateOrder_shouldAllowSameStatus() {

        order.setStatus(OrderStatus.PLACED);

        OrderRequestDto request =
                new OrderRequestDto();

        request.setCustomerId(1L);
        request.setStatus(OrderStatus.PLACED);

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(orderRepository.save(order))
                .thenReturn(order);

        when(orderMapper.toResponseDto(order))
                .thenReturn(orderResponseDto);

        OrderResponseDto result =
                orderService.createOrUpdateOrder(
                        1L,
                        request);

        assertEquals(
                OrderStatus.PLACED,
                order.getStatus());

        assertEquals(
                orderResponseDto,
                result);

        verify(orderRepository)
                .save(order);
    }

    @Test
    void cancelOrder_shouldCancelPlacedOrder() {

        order.setStatus(OrderStatus.PLACED);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(orderItemRepository.findByOrderId(1L))
                .thenReturn(List.of(orderItem));

        orderService.cancelOrder(1L);

        assertEquals(
                OrderStatus.CANCELLED,
                order.getStatus());

        assertEquals(
                12,
                product.getQuantity());

        verify(productRepository)
                .save(product);

        verify(orderRepository)
                .save(order);
    }

    @Test
    void cancelOrder_shouldDoNothingWhenAlreadyCancelled() {

        order.setStatus(OrderStatus.CANCELLED);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        orderService.cancelOrder(1L);

        assertEquals(
                OrderStatus.CANCELLED,
                order.getStatus());

        verify(orderItemRepository, never())
                .findByOrderId(1L);

        verify(orderRepository, never())
                .save(order);
    }

    @Test
    void cancelOrder_shouldThrowExceptionWhenStatusIsNotPlaced() {

        order.setStatus(OrderStatus.PAID);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        assertThrows(
                InvalidOrderStatusException.class,
                () -> orderService.cancelOrder(1L));

        verify(orderRepository, never())
                .save(order);
    }

    @Test
    void cancelOrder_shouldThrowExceptionWhenOrderNotFound() {

        when(orderRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                OrderNotFoundException.class,
                () -> orderService.cancelOrder(999L));

        verify(orderRepository, never())
                .save(any(Order.class));
    }

    @Test
    void deleteOrder_shouldDeletePlacedOrder() {

        order.setStatus(OrderStatus.PLACED);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        orderService.deleteOrder(1L);

        verify(orderRepository)
                .delete(order);
    }

    @Test
    void deleteOrder_shouldThrowExceptionWhenOrderNotFound() {

        when(orderRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                OrderNotFoundException.class,
                () -> orderService.deleteOrder(999L));

        verify(orderRepository, never())
                .delete(any(Order.class));
    }

    @Test
    void deleteOrder_shouldThrowExceptionWhenOrderIsNotPlaced() {

        order.setStatus(OrderStatus.PAID);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        assertThrows(
                InvalidOrderException.class,
                () -> orderService.deleteOrder(1L));

        verify(orderRepository, never())
                .delete(any(Order.class));
    }

    @Test
    void getAllOrders_shouldReturnAllOrders() {

        Order order2 = new Order();

        order2.setId(2L);
        order2.setCustomer(customer);
        order2.setStatus(OrderStatus.PAID);

        OrderResponseDto response2 =
                new OrderResponseDto();

        response2.setId(2L);

        when(orderRepository.findAll())
                .thenReturn(
                        List.of(
                                order,
                                order2));

        when(orderMapper.toResponseDto(order))
                .thenReturn(orderResponseDto);

        when(orderMapper.toResponseDto(order2))
                .thenReturn(response2);

        List<OrderResponseDto> result =
                orderService.getAllOrders();

        assertEquals(
                2,
                result.size());

        assertEquals(
                1L,
                result.get(0).getId());

        assertEquals(
                2L,
                result.get(1).getId());

        verify(orderRepository)
                .findAll();
    }

    @Test
    void getOrderById_shouldReturnOrder() {

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(orderMapper.toResponseDto(order))
                .thenReturn(orderResponseDto);

        OrderResponseDto result =
                orderService.getOrderById(1L);

        assertEquals(
                1L,
                result.getId());

        verify(orderRepository)
                .findById(1L);

        verify(orderMapper)
                .toResponseDto(order);
    }

    @Test
    void getOrderById_shouldThrowExceptionWhenOrderNotFound() {

        when(orderRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                OrderNotFoundException.class,
                () -> orderService.getOrderById(999L));

        verify(orderMapper, never())
                .toResponseDto(any(Order.class));
    }

    @Test
    void getOrdersByCustomerId_shouldReturnOrders() {

        Long customerId = 1L;

        Order order2 = new Order();

        order2.setId(2L);
        order2.setCustomer(customer);
        order2.setStatus(OrderStatus.PAID);

        OrderResponseDto response2 =
                new OrderResponseDto();

        response2.setId(2L);

        when(customerRepository.existsById(customerId))
                .thenReturn(true);

        when(orderRepository.findByCustomerId(customerId))
                .thenReturn(
                        List.of(
                                order,
                                order2));

        when(orderMapper.toResponseDto(order))
                .thenReturn(orderResponseDto);

        when(orderMapper.toResponseDto(order2))
                .thenReturn(response2);

        List<OrderResponseDto> result =
                orderService.getOrdersByCustomerId(
                        customerId);

        assertEquals(
                2,
                result.size());

        assertEquals(
                1L,
                result.get(0).getId());

        assertEquals(
                2L,
                result.get(1).getId());

        verify(customerRepository)
                .existsById(customerId);

        verify(orderRepository)
                .findByCustomerId(customerId);
    }

    @Test
    void getOrdersByCustomerId_shouldThrowExceptionWhenCustomerNotFound() {

        Long customerId = 999L;

        when(customerRepository.existsById(customerId))
                .thenReturn(false);

        assertThrows(
                CustomerNotFoundException.class,
                () -> orderService.getOrdersByCustomerId(
                        customerId));

        verify(customerRepository)
                .existsById(customerId);

        verify(orderRepository, never())
                .findByCustomerId(customerId);
    }
}