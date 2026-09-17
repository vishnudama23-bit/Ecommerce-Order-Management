package com.example.ecommerce.service;

import org.springframework.web.multipart.MultipartFile;

public interface ProductImageService {

    String saveImage(MultipartFile file);
}