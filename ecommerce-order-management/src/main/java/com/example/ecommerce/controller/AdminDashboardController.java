package com.example.ecommerce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecommerce.dto.response.AdminDashboardResponseDto;
import com.example.ecommerce.service.AdminDashboardService;

@RestController
@RequestMapping("/admin")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    public AdminDashboardController(
            AdminDashboardService adminDashboardService) {
        this.adminDashboardService = adminDashboardService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardResponseDto> getDashboard() {
        AdminDashboardResponseDto response =
                adminDashboardService.getDashboard();

        return ResponseEntity.ok(response);
    }
}