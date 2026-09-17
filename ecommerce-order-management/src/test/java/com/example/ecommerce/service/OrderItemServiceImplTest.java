package com.example.ecommerce.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.ecommerce.dto.request.OrderItemRequestDto;
import com.example.ecommerce.dto.response.OrderItemResponseDto;
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
import com.example.ecommerce.service.impl.OrderItemServiceImpl;

@ExtendWith(MockitoExtension.class)
class OrderItemServiceImplTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderItemMapper orderItemMapper;

    @InjectMocks
    private OrderItemServiceImpl orderItemService;

    private Order order;
    private Product product;
    private OrderItem orderItem;
    private OrderItemRequestDto request;
    private OrderItemResponseDto responseDto;

    @BeforeEach
    void setUp() {

        order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PLACED);
        order.setTotalAmount(new BigDecimal("1000"));

        product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setPrice(new BigDecimal("500"));
        product.setQuantity(10);

        orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(2);
        orderItem.setPrice(new BigDecimal("500"));

        request = new OrderItemRequestDto();
        request.setProductId(1L);
        request.setQuantity(2);

        responseDto = new OrderItemResponseDto();
        responseDto.setId(1L);
        responseDto.setQuantity(2);
        responseDto.setPrice(new BigDecimal("500"));
    }

    @Test
    void shouldCreateOrderItem() {

        OrderItem newOrderItem = new OrderItem();
        newOrderItem.setQuantity(2);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(orderItemMapper.toEntity(request))
                .thenReturn(newOrderItem);

        when(orderItemRepository.save(newOrderItem))
                .thenReturn(newOrderItem);

        when(orderItemMapper.toResponseDto(newOrderItem))
                .thenReturn(responseDto);

        OrderItemResponseDto result =
                orderItemService.createOrderItem(
                        1L,
                        request);

        assertEquals(
                8,
                product.getQuantity());

        assertEquals(
                new BigDecimal("2000"),
                order.getTotalAmount());

        assertEquals(
                order,
                newOrderItem.getOrder());

        assertEquals(
                product,
                newOrderItem.getProduct());

        assertEquals(
                new BigDecimal("500"),
                newOrderItem.getPrice());

        assertEquals(
                responseDto,
                result);

        verify(productRepository)
                .save(product);

        verify(orderItemRepository)
                .save(newOrderItem);

        verify(orderRepository)
                .save(order);
    }

    @Test
    void shouldCreateOrderItemWhenOrderTotalIsNull() {

        order.setTotalAmount(null);

        OrderItem newOrderItem = new OrderItem();
        newOrderItem.setQuantity(2);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(orderItemMapper.toEntity(request))
                .thenReturn(newOrderItem);

        when(orderItemRepository.save(newOrderItem))
                .thenReturn(newOrderItem);

        when(orderItemMapper.toResponseDto(newOrderItem))
                .thenReturn(responseDto);

        orderItemService.createOrderItem(
                1L,
                request);

        assertEquals(
                new BigDecimal("1000"),
                order.getTotalAmount());

        assertEquals(
                8,
                product.getQuantity());

        verify(orderRepository)
                .save(order);
    }

    @Test
    void shouldThrowExceptionWhenOrderNotFound() {

        when(orderRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                OrderNotFoundException.class,
                () -> orderItemService.createOrderItem(
                        99L,
                        request));

        verify(orderRepository)
                .findById(99L);

        verify(productRepository, never())
                .findById(any(Long.class));
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(productRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> orderItemService.createOrderItem(
                        1L,
                        request));

        verify(orderRepository)
                .findById(1L);

        verify(productRepository)
                .findById(1L);

        verify(orderItemRepository, never())
                .save(any(OrderItem.class));
    }

    @Test
    void shouldThrowExceptionWhenQuantityIsZero() {

        request.setQuantity(0);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        assertThrows(
                IllegalArgumentException.class,
                () -> orderItemService.createOrderItem(
                        1L,
                        request));

        assertEquals(
                10,
                product.getQuantity());

        verify(productRepository, never())
                .save(any(Product.class));

        verify(orderItemRepository, never())
                .save(any(OrderItem.class));
    }

    @Test
    void shouldThrowExceptionWhenQuantityIsNegative() {

        request.setQuantity(-2);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        assertThrows(
                IllegalArgumentException.class,
                () -> orderItemService.createOrderItem(
                        1L,
                        request));

        assertEquals(
                10,
                product.getQuantity());

        verify(productRepository, never())
                .save(any(Product.class));
    }

    @Test
    void shouldThrowExceptionWhenStockIsInsufficient() {

        request.setQuantity(20);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        assertThrows(
                InsufficientStockException.class,
                () -> orderItemService.createOrderItem(
                        1L,
                        request));

        assertEquals(
                10,
                product.getQuantity());

        verify(productRepository, never())
                .save(any(Product.class));

        verify(orderItemRepository, never())
                .save(any(OrderItem.class));
    }

    @Test
    void shouldGetOrderItemById() {

        when(orderItemRepository.findById(1L))
                .thenReturn(Optional.of(orderItem));

        when(orderItemMapper.toResponseDto(orderItem))
                .thenReturn(responseDto);

        OrderItemResponseDto result =
                orderItemService.getOrderItemById(1L);

        assertEquals(
                1L,
                result.getId());

        assertEquals(
                2,
                result.getQuantity());

        assertEquals(
                new BigDecimal("500"),
                result.getPrice());

        verify(orderItemRepository)
                .findById(1L);

        verify(orderItemMapper)
                .toResponseDto(orderItem);
    }

    @Test
    void shouldThrowExceptionWhenOrderItemNotFound() {

        when(orderItemRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                OrderItemNotFoundException.class,
                () -> orderItemService.getOrderItemById(99L));

        verify(orderItemRepository)
                .findById(99L);
    }

    @Test
    void shouldGetAllOrderItems() {

        OrderItem secondOrderItem = new OrderItem();
        secondOrderItem.setId(2L);
        secondOrderItem.setQuantity(1);
        secondOrderItem.setPrice(new BigDecimal("1000"));

        OrderItemResponseDto secondResponse =
                new OrderItemResponseDto();

        secondResponse.setId(2L);
        secondResponse.setQuantity(1);
        secondResponse.setPrice(
                new BigDecimal("1000"));

        when(orderItemRepository.findAll())
                .thenReturn(List.of(
                        orderItem,
                        secondOrderItem));

        when(orderItemMapper.toResponseDto(orderItem))
                .thenReturn(responseDto);

        when(orderItemMapper.toResponseDto(secondOrderItem))
                .thenReturn(secondResponse);

        List<OrderItemResponseDto> result =
                orderItemService.getAllOrderItems();

        assertEquals(
                2,
                result.size());

        assertEquals(
                1L,
                result.get(0).getId());

        assertEquals(
                2L,
                result.get(1).getId());

        verify(orderItemRepository)
                .findAll();
    }

    @Test
    void shouldDeleteOrderItemAndRestoreStock() {

        order.setStatus(OrderStatus.PLACED);
        order.setTotalAmount(
                new BigDecimal("2000"));

        product.setQuantity(8);

        when(orderItemRepository.findById(1L))
                .thenReturn(Optional.of(orderItem));

        orderItemService.deleteOrderItem(1L);

        assertEquals(
                10,
                product.getQuantity());

        assertEquals(
                new BigDecimal("1000"),
                order.getTotalAmount());

        verify(productRepository)
                .save(product);

        verify(orderRepository)
                .save(order);

        verify(orderItemRepository)
                .deleteById(1L);
    }

    @Test
    void shouldDeleteOrderItemWithoutRestoringStockWhenOrderCancelled() {

        order.setStatus(OrderStatus.CANCELLED);
        order.setTotalAmount(
                new BigDecimal("2000"));

        product.setQuantity(8);

        when(orderItemRepository.findById(1L))
                .thenReturn(Optional.of(orderItem));

        orderItemService.deleteOrderItem(1L);

        assertEquals(
                8,
                product.getQuantity());

        assertEquals(
                new BigDecimal("1000"),
                order.getTotalAmount());

        verify(productRepository, never())
                .save(product);

        verify(orderRepository)
                .save(order);

        verify(orderItemRepository)
                .deleteById(1L);
    }

    @Test
    void shouldSetOrderTotalToZeroWhenDeletingItemMakesTotalNegative() {

        order.setTotalAmount(
                new BigDecimal("500"));

        when(orderItemRepository.findById(1L))
                .thenReturn(Optional.of(orderItem));

        orderItemService.deleteOrderItem(1L);

        assertEquals(
                BigDecimal.ZERO,
                order.getTotalAmount());

        verify(orderRepository)
                .save(order);

        verify(orderItemRepository)
                .deleteById(1L);
    }

    @Test
    void shouldHandleNullOrderTotalWhenDeletingOrderItem() {

        order.setTotalAmount(null);

        when(orderItemRepository.findById(1L))
                .thenReturn(Optional.of(orderItem));

        orderItemService.deleteOrderItem(1L);

        assertEquals(
                BigDecimal.ZERO,
                order.getTotalAmount());

        verify(orderRepository)
                .save(order);

        verify(orderItemRepository)
                .deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingOrderItem() {

        when(orderItemRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                OrderItemNotFoundException.class,
                () -> orderItemService.deleteOrderItem(99L));

        verify(orderItemRepository)
                .findById(99L);

        verify(orderItemRepository, never())
                .deleteById(99L);
    }
}