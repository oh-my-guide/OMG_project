package com.example.omg_project.global.image.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {
    String upload(MultipartFile image); // 외부에서 사용할 public 메서드
    String uploadImage(MultipartFile image);
    void validateImageFileExtention(String filename);
    String uploadImageToS3(MultipartFile image) throws IOException;
}
