package com.example.ecommerce.service;

import java.util.List;

import com.example.ecommerce.dto.request.CustomerRequestDto;
import com.example.ecommerce.dto.response.CustomerResponseDto;

public interface CustomerService {

    CustomerResponseDto createOrUpdateCustomer(
            Long id,
            CustomerRequestDto request);

    List<CustomerResponseDto> getAllCustomers();

    CustomerResponseDto getCustomerById(Long id);

    void deleteCustomer(Long id);
}