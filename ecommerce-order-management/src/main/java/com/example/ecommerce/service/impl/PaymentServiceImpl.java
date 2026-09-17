package com.example.ecommerce.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ecommerce.dto.request.PaymentRequestDto;
import com.example.ecommerce.dto.response.PaymentResponseDto;
import com.example.ecommerce.entity.Customer;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.Payment;
import com.example.ecommerce.enums.OrderStatus;
import com.example.ecommerce.exception.InvalidPaymentException;
import com.example.ecommerce.exception.OrderNotFoundException;
import com.example.ecommerce.exception.PaymentNotFoundException;
import com.example.ecommerce.mapper.PaymentMapper;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.PaymentRepository;
import com.example.ecommerce.service.EmailService;
import com.example.ecommerce.service.PaymentService;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentMapper paymentMapper;
    private final EmailService emailService;

    public PaymentServiceImpl(
            PaymentRepository paymentRepository,
            OrderRepository orderRepository,
            PaymentMapper paymentMapper,
            EmailService emailService) {

        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.paymentMapper = paymentMapper;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public PaymentResponseDto createOrUpdatePayment(
            Long id,
            PaymentRequestDto request) {

        Order order =
                orderRepository.findById(request.getOrderId())
                        .orElseThrow(() ->
                                new OrderNotFoundException(
                                        "Order not found with id: "
                                                + request.getOrderId()));

        validateOrderOwnership(order);

        if (id == null) {

            if (order.getStatus() != OrderStatus.PLACED) {
                throw new InvalidPaymentException(
                        "Payment can be made only for PLACED orders");
            }

            if (paymentRepository.existsByOrderId(order.getId())) {
                throw new InvalidPaymentException(
                        "Payment already exists for order id: "
                                + order.getId());
            }

            if (request.getAmount() == null) {
                throw new InvalidPaymentException(
                        "Payment amount is required");
            }

            if (order.getTotalAmount() == null) {
                throw new InvalidPaymentException(
                        "Order total amount is not available");
            }

            if (request.getAmount()
                    .compareTo(order.getTotalAmount()) != 0) {

                throw new InvalidPaymentException(
                        "Payment amount must match order total amount. "
                                + "Order total: "
                                + order.getTotalAmount());
            }

            Payment payment =
                    paymentMapper.toEntity(request);

            payment.setPaymentDate(
                    LocalDateTime.now());

            payment.setStatus("SUCCESS");

            payment.setOrder(order);

            Payment savedPayment =
                    paymentRepository.save(payment);

            order.setStatus(OrderStatus.PAID);

            orderRepository.save(order);

            sendPaymentSuccessEmail(
                    order,
                    savedPayment);

            return paymentMapper.toResponseDto(
                    savedPayment);
        }

        Payment payment =
                paymentRepository.findById(id)
                        .orElseThrow(() ->
                                new PaymentNotFoundException(
                                        "Payment not found with id: "
                                                + id));

        validatePaymentOwnership(payment);

        if ("SUCCESS".equals(payment.getStatus())) {
            throw new InvalidPaymentException(
                    "Successful payment cannot be updated");
        }

        payment.setAmount(
                request.getAmount());

        payment.setPaymentMethod(
                request.getPaymentMethod());

        payment.setOrder(order);

        Payment savedPayment =
                paymentRepository.save(payment);

        return paymentMapper.toResponseDto(
                savedPayment);
    }

    @Override
    public List<PaymentResponseDto> getAllPayments() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        List<Payment> payments;

        if (isAdmin(authentication)) {

            payments =
                    paymentRepository.findAll();

        } else {

            payments =
                    paymentRepository
                            .findByOrderCustomerUserEmail(
                                    authentication.getName());
        }

        return payments.stream()
                .map(paymentMapper::toResponseDto)
                .toList();
    }

    @Override
    public PaymentResponseDto getPaymentById(
            Long id) {

        Payment payment =
                paymentRepository.findById(id)
                        .orElseThrow(() ->
                                new PaymentNotFoundException(
                                        "Payment not found with id: "
                                                + id));

        validatePaymentOwnership(payment);

        return paymentMapper.toResponseDto(
                payment);
    }

    @Override
    @Transactional
    public void deletePayment(Long id) {

        Payment payment =
                paymentRepository.findById(id)
                        .orElseThrow(() ->
                                new PaymentNotFoundException(
                                        "Payment not found with id: "
                                                + id));

        validatePaymentOwnership(payment);

        if ("SUCCESS".equals(payment.getStatus())) {
            throw new InvalidPaymentException(
                    "Successful payment cannot be deleted");
        }

        paymentRepository.delete(payment);
    }

    private void sendPaymentSuccessEmail(
            Order order,
            Payment payment) {

        if (order.getCustomer() == null) {
            return;
        }

        Customer customer =
                order.getCustomer();

        String email =
                customer.getEmail();

        if (email == null || email.isBlank()) {
            return;
        }

        String subject =
                "Payment Successful - Order #"
                        + order.getId();

        String body =
                "Hello "
                        + customer.getName()
                        + ",\n\n"
                        + "Your payment was successful.\n\n"
                        + "Order ID: "
                        + order.getId()
                        + "\n"
                        + "Payment ID: "
                        + payment.getId()
                        + "\n"
                        + "Amount: ₹"
                        + payment.getAmount()
                        + "\n"
                        + "Payment Method: "
                        + payment.getPaymentMethod()
                        + "\n"
                        + "Payment Status: "
                        + payment.getStatus()
                        + "\n\n"
                        + "Thank you for shopping with us.";

        emailService.sendEmail(
                email,
                subject,
                body);
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
            throw new InvalidPaymentException(
                    "Order does not have a customer");
        }

        Customer customer =
                order.getCustomer();

        if (customer.getUser() == null) {
            throw new InvalidPaymentException(
                    "Customer is not linked to a user");
        }

        if (!customer.getUser()
                .getEmail()
                .equals(authentication.getName())) {

            throw new InvalidPaymentException(
                    "You are not allowed to access this order");
        }
    }

    private void validatePaymentOwnership(
            Payment payment) {

        if (payment.getOrder() == null) {
            throw new InvalidPaymentException(
                    "Payment does not have an order");
        }

        validateOrderOwnership(
                payment.getOrder());
    }

    private boolean isAdmin(
            Authentication authentication) {

        if (authentication == null) {
            return false;
        }

        return authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority()
                                .equals("ROLE_ADMIN"));
    }
}