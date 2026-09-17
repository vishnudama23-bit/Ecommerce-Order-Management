package com.example.ecommerce.mapper;

import org.mapstruct.Mapper;

import com.example.ecommerce.dto.request.CustomerRequestDto;
import com.example.ecommerce.dto.response.CustomerResponseDto;
import com.example.ecommerce.entity.Customer;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    Customer toEntity(CustomerRequestDto dto);

    CustomerResponseDto toResponseDto(Customer customer);
}