package com.example.ecommerce.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecommerce.dto.request.CustomerRequestDto;
import com.example.ecommerce.dto.response.CustomerResponseDto;
import com.example.ecommerce.service.CustomerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<?> createCustomer(
            @Valid @RequestBody CustomerRequestDto request) {

        CustomerResponseDto response =
                customerService.createOrUpdateCustomer(
                        null,
                        request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<?> getAllCustomers() {

        List<CustomerResponseDto> response =
                customerService.getAllCustomers();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerById(
            @PathVariable Long id) {

        CustomerResponseDto response =
                customerService.getCustomerById(id);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequestDto request) {

        CustomerResponseDto response =
                customerService.createOrUpdateCustomer(
                        id,
                        request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomer(
            @PathVariable Long id) {

        customerService.deleteCustomer(id);

        return ResponseEntity.noContent().build();
    }
}