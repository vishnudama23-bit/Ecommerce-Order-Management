package com.example.ecommerce.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.ecommerce.entity.User;
import com.example.ecommerce.security.JwtService;

class JwtServiceTest {

    private JwtService jwtService;
    private User user;

    @BeforeEach
    void setUp() {

        jwtService = new JwtService();

        user = new User();
        user.setId(1L);
        user.setName("John");
        user.setEmail("john@gmail.com");
        user.setPassword("encodedPassword");
        user.setRole("USER");
    }

    @Test
    void shouldGenerateToken() {

        String token =
                jwtService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void shouldExtractEmailFromToken() {

        String token =
                jwtService.generateToken(user);

        String email =
                jwtService.extractEmail(token);

        assertEquals(
                "john@gmail.com",
                email);
    }

    @Test
    void shouldReturnTrueWhenTokenIsValid() {

        String token =
                jwtService.generateToken(user);

        boolean result =
                jwtService.isTokenValid(
                        token,
                        "john@gmail.com");

        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenEmailDoesNotMatch() {

        String token =
                jwtService.generateToken(user);

        boolean result =
                jwtService.isTokenValid(
                        token,
                        "wrong@gmail.com");

        assertFalse(result);
    }

    @Test
    void shouldThrowExceptionWhenTokenIsInvalid() {

        String invalidToken =
                "invalid.jwt.token";

        assertThrows(
                Exception.class,
                () -> jwtService.extractEmail(
                        invalidToken));
    }

    @Test
    void shouldGenerateDifferentTokensForDifferentUsers() {

        User secondUser = new User();
        secondUser.setId(2L);
        secondUser.setName("David");
        secondUser.setEmail("david@gmail.com");
        secondUser.setPassword("encodedPassword");
        secondUser.setRole("USER");

        String firstToken =
                jwtService.generateToken(user);

        String secondToken =
                jwtService.generateToken(secondUser);

        assertNotNull(firstToken);
        assertNotNull(secondToken);

        assertFalse(
                firstToken.equals(secondToken));
    }

    @Test
    void shouldExtractCorrectEmailForSecondUser() {

        User secondUser = new User();
        secondUser.setId(2L);
        secondUser.setName("David");
        secondUser.setEmail("david@gmail.com");
        secondUser.setPassword("encodedPassword");
        secondUser.setRole("USER");

        String token =
                jwtService.generateToken(secondUser);

        String email =
                jwtService.extractEmail(token);

        assertEquals(
                "david@gmail.com",
                email);
    }
}