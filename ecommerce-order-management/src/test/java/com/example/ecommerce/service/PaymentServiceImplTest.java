package com.example.ecommerce.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
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

import com.example.ecommerce.dto.request.PaymentRequestDto;
import com.example.ecommerce.dto.response.PaymentResponseDto;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.Payment;
import com.example.ecommerce.enums.OrderStatus;
import com.example.ecommerce.exception.InvalidPaymentException;
import com.example.ecommerce.exception.OrderNotFoundException;
import com.example.ecommerce.exception.PaymentNotFoundException;
import com.example.ecommerce.mapper.PaymentMapper;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.PaymentRepository;
import com.example.ecommerce.service.impl.PaymentServiceImpl;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Order order;
    private Payment payment;
    private PaymentResponseDto responseDto;
    private PaymentRequestDto request;

    @BeforeEach
    void setUp() {

        order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PLACED);
        order.setTotalAmount(new BigDecimal("1500"));

        payment = new Payment();
        payment.setId(1L);
        payment.setOrder(order);
        payment.setAmount(new BigDecimal("1500"));
        payment.setPaymentMethod("UPI");
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus("SUCCESS");

        responseDto = new PaymentResponseDto();
        responseDto.setId(1L);
        responseDto.setAmount(new BigDecimal("1500"));
        responseDto.setPaymentMethod("UPI");
        responseDto.setStatus("SUCCESS");

        request = new PaymentRequestDto();
        request.setOrderId(1L);
        request.setPaymentMethod("UPI");
    }

    @Test
    void shouldCreatePayment() {

        Payment newPayment = new Payment();

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(paymentRepository.existsByOrderId(1L))
                .thenReturn(false);

        when(paymentMapper.toEntity(request))
                .thenReturn(newPayment);

        when(paymentRepository.save(any(Payment.class)))
                .thenReturn(payment);

        when(paymentMapper.toResponseDto(payment))
                .thenReturn(responseDto);

        PaymentResponseDto result =
                paymentService.createOrUpdatePayment(
                        null,
                        request);

        assertEquals(
                1L,
                result.getId());

        assertEquals(
                new BigDecimal("1500"),
                result.getAmount());

        assertEquals(
                "UPI",
                result.getPaymentMethod());

        assertEquals(
                "SUCCESS",
                result.getStatus());

        assertEquals(
                OrderStatus.PAID,
                order.getStatus());

        verify(orderRepository).findById(1L);
        verify(paymentRepository).existsByOrderId(1L);
        verify(paymentMapper).toEntity(request);
        verify(paymentRepository).save(any(Payment.class));
        verify(orderRepository).save(order);
        verify(paymentMapper).toResponseDto(payment);
    }

    @Test
    void shouldThrowExceptionWhenOrderNotFound() {

        when(orderRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                OrderNotFoundException.class,
                () -> paymentService.createOrUpdatePayment(
                        null,
                        request));

        verify(orderRepository).findById(1L);

        verify(paymentRepository, never())
                .existsByOrderId(1L);
    }

    @Test
    void shouldThrowExceptionWhenPaymentIsNotAllowedForNonPlacedOrder() {

        order.setStatus(OrderStatus.PAID);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        assertThrows(
                InvalidPaymentException.class,
                () -> paymentService.createOrUpdatePayment(
                        null,
                        request));

        verify(orderRepository).findById(1L);

        verify(paymentRepository, never())
                .existsByOrderId(1L);
    }

    @Test
    void shouldThrowExceptionWhenPaymentAlreadyExists() {

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(paymentRepository.existsByOrderId(1L))
                .thenReturn(true);

        assertThrows(
                InvalidPaymentException.class,
                () -> paymentService.createOrUpdatePayment(
                        null,
                        request));

        verify(orderRepository).findById(1L);

        verify(paymentRepository)
                .existsByOrderId(1L);

        verify(paymentRepository, never())
                .save(any(Payment.class));
    }

    @Test
    void shouldGetPaymentById() {

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.of(payment));

        when(paymentMapper.toResponseDto(payment))
                .thenReturn(responseDto);

        PaymentResponseDto result =
                paymentService.getPaymentById(1L);

        assertEquals(
                1L,
                result.getId());

        assertEquals(
                "SUCCESS",
                result.getStatus());

        assertEquals(
                new BigDecimal("1500"),
                result.getAmount());

        verify(paymentRepository)
                .findById(1L);

        verify(paymentMapper)
                .toResponseDto(payment);
    }

    @Test
    void shouldThrowExceptionWhenPaymentNotFound() {

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                PaymentNotFoundException.class,
                () -> paymentService.getPaymentById(1L));

        verify(paymentRepository)
                .findById(1L);
    }

    @Test
    void shouldGetAllPayments() {

        Payment payment2 = new Payment();
        payment2.setId(2L);
        payment2.setAmount(new BigDecimal("2500"));
        payment2.setPaymentMethod("CARD");
        payment2.setStatus("SUCCESS");

        PaymentResponseDto responseDto2 =
                new PaymentResponseDto();

        responseDto2.setId(2L);
        responseDto2.setAmount(new BigDecimal("2500"));
        responseDto2.setPaymentMethod("CARD");
        responseDto2.setStatus("SUCCESS");

        when(paymentRepository.findAll())
                .thenReturn(List.of(
                        payment,
                        payment2));

        when(paymentMapper.toResponseDto(payment))
                .thenReturn(responseDto);

        when(paymentMapper.toResponseDto(payment2))
                .thenReturn(responseDto2);

        List<PaymentResponseDto> result =
                paymentService.getAllPayments();

        assertEquals(
                2,
                result.size());

        assertEquals(
                1L,
                result.get(0).getId());

        assertEquals(
                2L,
                result.get(1).getId());

        verify(paymentRepository)
                .findAll();

        verify(paymentMapper)
                .toResponseDto(payment);

        verify(paymentMapper)
                .toResponseDto(payment2);
    }

    @Test
    void shouldUpdatePaymentWhenPaymentIsNotSuccessful() {

        payment.setStatus("FAILED");

        request.setPaymentMethod("CARD");

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.of(payment));

        when(paymentRepository.save(payment))
                .thenReturn(payment);

        when(paymentMapper.toResponseDto(payment))
                .thenReturn(responseDto);

        PaymentResponseDto result =
                paymentService.createOrUpdatePayment(
                        1L,
                        request);

        assertEquals(
                "CARD",
                payment.getPaymentMethod());

        assertEquals(
                responseDto,
                result);

        verify(orderRepository)
                .findById(1L);

        verify(paymentRepository)
                .findById(1L);

        verify(paymentRepository)
                .save(payment);

        verify(paymentMapper)
                .toResponseDto(payment);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingSuccessfulPayment() {

        payment.setStatus("SUCCESS");

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.of(payment));

        assertThrows(
                InvalidPaymentException.class,
                () -> paymentService.createOrUpdatePayment(
                        1L,
                        request));

        verify(orderRepository)
                .findById(1L);

        verify(paymentRepository)
                .findById(1L);

        verify(paymentRepository, never())
                .save(any(Payment.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingPayment() {

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(paymentRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                PaymentNotFoundException.class,
                () -> paymentService.createOrUpdatePayment(
                        99L,
                        request));

        verify(orderRepository)
                .findById(1L);

        verify(paymentRepository)
                .findById(99L);
    }

    @Test
    void shouldDeletePaymentWhenPaymentIsNotSuccessful() {

        payment.setStatus("FAILED");

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.of(payment));

        paymentService.deletePayment(1L);

        verify(paymentRepository)
                .findById(1L);

        verify(paymentRepository)
                .delete(payment);
    }

    @Test
    void shouldThrowExceptionWhenDeletingSuccessfulPayment() {

        payment.setStatus("SUCCESS");

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.of(payment));

        assertThrows(
                InvalidPaymentException.class,
                () -> paymentService.deletePayment(1L));

        verify(paymentRepository)
                .findById(1L);

        verify(paymentRepository, never())
                .delete(payment);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingPayment() {

        when(paymentRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                PaymentNotFoundException.class,
                () -> paymentService.deletePayment(99L));

        verify(paymentRepository)
                .findById(99L);

        verify(paymentRepository, never())
                .delete(any(Payment.class));
    }
}