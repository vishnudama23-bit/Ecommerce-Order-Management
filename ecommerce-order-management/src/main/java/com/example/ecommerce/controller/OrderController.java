package com.example.ecommerce.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecommerce.dto.request.OrderRequestDto;
import com.example.ecommerce.dto.response.OrderResponseDto;
import com.example.ecommerce.enums.OrderStatus;
import com.example.ecommerce.service.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(
            @Valid @RequestBody OrderRequestDto request) {

        OrderResponseDto response =
                orderService.createOrUpdateOrder(
                        null,
                        request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponseDto> updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody OrderRequestDto request) {

        OrderResponseDto response =
                orderService.createOrUpdateOrder(
                        id,
                        request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                orderService.getOrderById(id));
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponseDto>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort sort =
                direction.equalsIgnoreCase("desc")
                        ? Sort.by(sortBy).descending()
                        : Sort.by(sortBy).ascending();

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        sort);

        return ResponseEntity.ok(
                orderService.getAllOrders(pageable));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderResponseDto>>
            getOrdersByCustomer(
                    @PathVariable Long customerId) {

        return ResponseEntity.ok(
                orderService.getOrdersByCustomer(
                        customerId));
    }

    @GetMapping("/customer/{customerId}/page")
    public ResponseEntity<Page<OrderResponseDto>>
            getOrdersByCustomerWithPagination(
                    @PathVariable Long customerId,
                    @RequestParam(defaultValue = "0") int page,
                    @RequestParam(defaultValue = "10") int size,
                    @RequestParam(defaultValue = "id") String sortBy,
                    @RequestParam(defaultValue = "asc") String direction) {

        Sort sort =
                direction.equalsIgnoreCase("desc")
                        ? Sort.by(sortBy).descending()
                        : Sort.by(sortBy).ascending();

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        sort);

        return ResponseEntity.ok(
                orderService.getOrdersByCustomer(
                        customerId,
                        pageable));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponseDto>
            updateOrderStatus(
                    @PathVariable Long id,
                    @RequestParam OrderStatus status) {

        return ResponseEntity.ok(
                orderService.updateOrderStatus(
                        id,
                        status));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable Long id) {

        orderService.cancelOrder(id);

        return ResponseEntity
                .noContent()
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(
            @PathVariable Long id) {

        orderService.deleteOrder(id);

        return ResponseEntity
                .noContent()
                .build();
    }
    @GetMapping("/search")
    public ResponseEntity<List<OrderResponseDto>> searchOrdersByStatus(
            @RequestParam OrderStatus status) {

        List<OrderResponseDto> response =
                orderService.searchOrdersByStatus(status);

        return ResponseEntity.ok(response);
    }
    
}