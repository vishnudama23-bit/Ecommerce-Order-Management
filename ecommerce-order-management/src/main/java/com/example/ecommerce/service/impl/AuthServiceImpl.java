package com.example.ecommerce.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ecommerce.dto.request.LoginRequestDto;
import com.example.ecommerce.dto.request.RegisterRequestDto;
import com.example.ecommerce.dto.response.AuthResponseDto;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.security.JwtService;
import com.example.ecommerce.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional
    public AuthResponseDto register(
            RegisterRequestDto request) {

        if (userRepository.existsByEmail(
                request.getEmail())) {

            throw new RuntimeException(
                    "Email already registered");
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()));

        user.setRole("USER");

        User savedUser =
                userRepository.save(user);

        return new AuthResponseDto(
                null,
                null,
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole()
        );
    }

    @Override
    public AuthResponseDto login(
            LoginRequestDto request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user =
                userRepository.findByEmail(
                        request.getEmail())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"));

        String token =
                jwtService.generateToken(user);

        return new AuthResponseDto(
                token,
                "Bearer",
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
}