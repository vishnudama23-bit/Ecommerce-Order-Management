package com.example.ecommerce.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.ecommerce.security.CustomUserDetailsService;
import com.example.ecommerce.security.JwtAuthenticationFilter;
import com.example.ecommerce.security.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldContinueFilterWhenAuthorizationHeaderIsMissing()
            throws ServletException, IOException {

        jwtAuthenticationFilter.doFilter(
                request,
                response,
                filterChain);

        verify(filterChain)
                .doFilter(request, response);

        verify(jwtService, never())
                .extractEmail(any(String.class));

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication());
    }

    @Test
    void shouldContinueFilterWhenAuthorizationHeaderIsNotBearer()
            throws ServletException, IOException {

        request.addHeader(
                "Authorization",
                "Basic username:password");

        jwtAuthenticationFilter.doFilter(
                request,
                response,
                filterChain);

        verify(filterChain)
                .doFilter(request, response);

        verify(jwtService, never())
                .extractEmail(any(String.class));

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication());
    }

    @Test
    void shouldContinueFilterWhenJwtIsInvalid()
            throws ServletException, IOException {

        request.addHeader(
                "Authorization",
                "Bearer invalid-token");

        when(jwtService.extractEmail("invalid-token"))
                .thenThrow(new RuntimeException(
                        "Invalid token"));

        jwtAuthenticationFilter.doFilter(
                request,
                response,
                filterChain);

        verify(jwtService)
                .extractEmail("invalid-token");

        verify(filterChain)
                .doFilter(request, response);

        verify(userDetailsService, never())
                .loadUserByUsername(any(String.class));

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication());
    }

    @Test
    void shouldAuthenticateUserWhenJwtIsValid()
            throws ServletException, IOException {

        request.addHeader(
                "Authorization",
                "Bearer valid-token");

        when(jwtService.extractEmail("valid-token"))
                .thenReturn("john@gmail.com");

        when(userDetailsService.loadUserByUsername(
                "john@gmail.com"))
                .thenReturn(userDetails);

        when(userDetails.getUsername())
                .thenReturn("john@gmail.com");

        when(userDetails.getAuthorities())
                .thenReturn(Collections.emptyList());

        when(jwtService.isTokenValid(
                "valid-token",
                "john@gmail.com"))
                .thenReturn(true);

        jwtAuthenticationFilter.doFilter(
                request,
                response,
                filterChain);

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        assertEquals(
                userDetails,
                authentication.getPrincipal());

        verify(jwtService)
                .extractEmail("valid-token");

        verify(userDetailsService)
                .loadUserByUsername("john@gmail.com");

        verify(jwtService)
                .isTokenValid(
                        "valid-token",
                        "john@gmail.com");

        verify(filterChain)
                .doFilter(request, response);
    }

    @Test
    void shouldNotAuthenticateWhenJwtIsInvalid()
            throws ServletException, IOException {

        request.addHeader(
                "Authorization",
                "Bearer invalid-token");

        when(jwtService.extractEmail("invalid-token"))
                .thenReturn("john@gmail.com");

        when(userDetailsService.loadUserByUsername(
                "john@gmail.com"))
                .thenReturn(userDetails);

        when(userDetails.getUsername())
                .thenReturn("john@gmail.com");

        when(jwtService.isTokenValid(
                "invalid-token",
                "john@gmail.com"))
                .thenReturn(false);

        jwtAuthenticationFilter.doFilter(
                request,
                response,
                filterChain);

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication());

        verify(jwtService)
                .extractEmail("invalid-token");

        verify(userDetailsService)
                .loadUserByUsername("john@gmail.com");

        verify(jwtService)
                .isTokenValid(
                        "invalid-token",
                        "john@gmail.com");

        verify(filterChain)
                .doFilter(request, response);
    }

    @Test
    void shouldNotAuthenticateWhenAuthenticationAlreadyExists()
            throws ServletException, IOException {

        request.addHeader(
                "Authorization",
                "Bearer valid-token");

        Authentication existingAuthentication =
                org.mockito.Mockito.mock(
                        Authentication.class);

        SecurityContextHolder
                .getContext()
                .setAuthentication(
                        existingAuthentication);

        when(jwtService.extractEmail("valid-token"))
                .thenReturn("john@gmail.com");

        jwtAuthenticationFilter.doFilter(
                request,
                response,
                filterChain);

        assertEquals(
                existingAuthentication,
                SecurityContextHolder
                        .getContext()
                        .getAuthentication());

        verify(jwtService)
                .extractEmail("valid-token");

        verify(userDetailsService, never())
                .loadUserByUsername(any(String.class));

        verify(filterChain)
                .doFilter(request, response);
    }
}