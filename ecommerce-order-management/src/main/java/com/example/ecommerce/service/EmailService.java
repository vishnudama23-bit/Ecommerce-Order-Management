package com.example.ecommerce.service;

public interface EmailService {

    void sendEmail(
            String to,
            String subject,
            String body);
}