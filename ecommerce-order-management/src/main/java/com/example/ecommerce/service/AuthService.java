package com.example.ecommerce.service;

import com.example.ecommerce.dto.request.LoginRequestDto;
import com.example.ecommerce.dto.request.RegisterRequestDto;
import com.example.ecommerce.dto.response.AuthResponseDto;

public interface AuthService {

    AuthResponseDto register(
            RegisterRequestDto request);

    AuthResponseDto login(
            LoginRequestDto request);
}