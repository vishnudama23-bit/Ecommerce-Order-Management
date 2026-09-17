package com.example.ecommerce.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.ecommerce.dto.request.PaymentRequestDto;
import com.example.ecommerce.dto.response.PaymentResponseDto;
import com.example.ecommerce.entity.Payment;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(source = "order.id", target = "orderId")
    PaymentResponseDto toResponseDto(
            Payment payment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "paymentDate", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "order", ignore = true)
    Payment toEntity(
            PaymentRequestDto request);
}