package com.example.ecommerce.scheduler;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.ecommerce.entity.Product;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.EmailService;

@Component
public class LowStockScheduler {

    private final ProductRepository productRepository;
    private final EmailService emailService;

    @Value("${app.admin.email}")
    private String adminEmail;

    public LowStockScheduler(
            ProductRepository productRepository,
            EmailService emailService) {
        this.productRepository = productRepository;
        this.emailService = emailService;
    }

    @Scheduled(cron = "0 0 9 * * *")
    public void checkLowStock() {

        List<Product> products =
                productRepository.findAll();

        for (Product product : products) {

            if (product.getQuantity() != null
                    && product.getQuantity() <= 5) {

                String subject =
                        "Low Stock Alert - "
                                + product.getName();

                String body =
                        "Low stock alert.\n\n"
                                + "Product: "
                                + product.getName()
                                + "\n"
                                + "Current Stock: "
                                + product.getQuantity();

                emailService.sendEmail(
                        adminEmail,
                        subject,
                        body);
            }
        }
    }
}