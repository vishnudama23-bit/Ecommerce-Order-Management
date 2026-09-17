package com.example.ecommerce.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.ecommerce.service.ProductImageService;

@Service
public class ProductImageServiceImpl
        implements ProductImageService {

    private final Path uploadDirectory =
            Paths.get("uploads/products");

    @Override
    public String saveImage(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Image file is required");
        }

        String originalFileName =
                file.getOriginalFilename();

        if (originalFileName == null
                || originalFileName.isBlank()) {

            throw new IllegalArgumentException(
                    "Invalid image file name");
        }

        String fileExtension = "";

        int extensionIndex =
                originalFileName.lastIndexOf(".");

        if (extensionIndex >= 0) {
            fileExtension =
                    originalFileName.substring(
                            extensionIndex);
        }

        String fileName =
                UUID.randomUUID()
                        + fileExtension;

        try {

            Files.createDirectories(
                    uploadDirectory);

            Path filePath =
                    uploadDirectory.resolve(fileName);

            Files.copy(
                    file.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING);

            return "/images/products/" + fileName;

        } catch (IOException ex) {

            throw new RuntimeException(
                    "Failed to save product image",
                    ex);
        }
    }
}