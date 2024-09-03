package com.example.omg_project.global.image.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {
    String upload(MultipartFile image);
    String upload(MultipartFile image, String directoryName);  // S3 버킷의 폴더 이름을 포함한 upload 메서드 오버로딩
    String uploadImage(MultipartFile image, String directoryName);
    void validateImageFileExtention(String filename);
    String uploadImageToS3(MultipartFile image, String directoryName) throws IOException;
    void deleteImageFromS3(String imageUrl);
    String getKeyFromImageUrl(String imageUrl);
}
