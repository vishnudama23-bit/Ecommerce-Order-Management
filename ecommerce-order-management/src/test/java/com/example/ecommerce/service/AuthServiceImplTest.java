package com.example.ecommerce.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.ecommerce.dto.request.LoginRequestDto;
import com.example.ecommerce.dto.request.RegisterRequestDto;
import com.example.ecommerce.dto.response.AuthResponseDto;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.security.JwtService;
import com.example.ecommerce.service.impl.AuthServiceImpl;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequestDto registerRequest;
    private LoginRequestDto loginRequest;
    private User user;

    @BeforeEach
    void setUp() {

        registerRequest = new RegisterRequestDto();
        registerRequest.setName("John");
        registerRequest.setEmail("john@gmail.com");
        registerRequest.setPassword("john123");

        loginRequest = new LoginRequestDto();
        loginRequest.setEmail("john@gmail.com");
        loginRequest.setPassword("john123");

        user = new User();
        user.setId(1L);
        user.setName("John");
        user.setEmail("john@gmail.com");
        user.setPassword("encodedPassword");
        user.setRole("USER");
    }

    @Test
    void shouldRegisterUser() {

        when(userRepository.existsByEmail(
                "john@gmail.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("john123"))
                .thenReturn("encodedPassword");

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        AuthResponseDto result =
                authService.register(registerRequest);

        assertEquals(
                1L,
                result.getUserId());

        assertEquals(
                "John",
                result.getName());

        assertEquals(
                "john@gmail.com",
                result.getEmail());

        assertEquals(
                "USER",
                result.getRole());

        assertEquals(
                null,
                result.getToken());

        assertEquals(
                null,
                result.getTokenType());

        verify(userRepository)
                .existsByEmail("john@gmail.com");

        verify(passwordEncoder)
                .encode("john123");

        verify(userRepository)
                .save(any(User.class));
    }

    @Test
    void shouldEncodePasswordDuringRegistration() {

        when(userRepository.existsByEmail(
                "john@gmail.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("john123"))
                .thenReturn("encodedPassword");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        AuthResponseDto result =
                authService.register(registerRequest);

        assertEquals(
                "john@gmail.com",
                result.getEmail());

        verify(passwordEncoder)
                .encode("john123");

        verify(userRepository)
                .save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyRegistered() {

        when(userRepository.existsByEmail(
                "john@gmail.com"))
                .thenReturn(true);

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> authService.register(
                                registerRequest));

        assertEquals(
                "Email already registered",
                exception.getMessage());

        verify(userRepository)
                .existsByEmail("john@gmail.com");

        verify(passwordEncoder, never())
                .encode(any(String.class));

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void shouldAssignUserRoleDuringRegistration() {

        when(userRepository.existsByEmail(
                "john@gmail.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("john123"))
                .thenReturn("encodedPassword");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        AuthResponseDto result =
                authService.register(registerRequest);

        assertEquals(
                "USER",
                result.getRole());

        verify(userRepository)
                .save(any(User.class));
    }

    @Test
    void shouldLoginUser() {

        when(userRepository.findByEmail(
                "john@gmail.com"))
                .thenReturn(Optional.of(user));

        when(jwtService.generateToken(user))
                .thenReturn("jwt-token");

        AuthResponseDto result =
                authService.login(loginRequest);

        assertEquals(
                "jwt-token",
                result.getToken());

        assertEquals(
                "Bearer",
                result.getTokenType());

        assertEquals(
                1L,
                result.getUserId());

        assertEquals(
                "John",
                result.getName());

        assertEquals(
                "john@gmail.com",
                result.getEmail());

        assertEquals(
                "USER",
                result.getRole());

        verify(authenticationManager)
                .authenticate(any(
                        UsernamePasswordAuthenticationToken.class));

        verify(userRepository)
                .findByEmail("john@gmail.com");

        verify(jwtService)
                .generateToken(user);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundDuringLogin() {

        when(userRepository.findByEmail(
                "john@gmail.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> authService.login(loginRequest));

        verify(authenticationManager)
                .authenticate(any(
                        UsernamePasswordAuthenticationToken.class));

        verify(userRepository)
                .findByEmail("john@gmail.com");

        verify(jwtService, never())
                .generateToken(any(User.class));
    }

    @Test
    void shouldAuthenticateUserDuringLogin() {

        when(userRepository.findByEmail(
                "john@gmail.com"))
                .thenReturn(Optional.of(user));

        when(jwtService.generateToken(user))
                .thenReturn("jwt-token");

        authService.login(loginRequest);

        verify(authenticationManager)
                .authenticate(any(
                        UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void shouldGenerateJwtTokenDuringLogin() {

        when(userRepository.findByEmail(
                "john@gmail.com"))
                .thenReturn(Optional.of(user));

        when(jwtService.generateToken(user))
                .thenReturn("jwt-token");

        AuthResponseDto result =
                authService.login(loginRequest);

        assertEquals(
                "jwt-token",
                result.getToken());

        verify(jwtService)
                .generateToken(user);
    }
}