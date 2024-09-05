package com.example.omg_project.global.image.controller;

import com.example.omg_project.global.exception.CustomException;
import com.example.omg_project.global.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    /**
     * CKEditor 이미지 업로드 핸들러
     * @param request MultipartRequest 객체
     * @return 업로드 결과와 이미지 URL을 포함하는 ResponseEntity 객체
     */
    @PostMapping("/image/upload")
    public ResponseEntity<Map<String, Object>> imageUpload(MultipartRequest request){
        // CKEditor에서 이미지를 올리면 두 가지 응답 값을 받음 (uploaded: 업로드가 잘 되었는지, url: 어디에 업로드되었는지)
        Map<String, Object> responseData = new HashMap<>();

        // CKEditor에서 이미지 파일을 보내줄 때 'upload'라는 key에 이미지 파일을 저장해서 보내줌 -> request에서 이미지 파일 뽑아냄
        MultipartFile image = request.getFile("upload");

        try {
            // S3 버킷에 업로드
            String s3Url = imageService.upload(image, "Post");

            // 제대로 업로드가 되었다면 uploaded=true와 url 설정 해줌
            responseData.put("uploaded", true);
            responseData.put("url", s3Url);

            return ResponseEntity.ok(responseData);
        } catch (CustomException e) {
            responseData.put("uploaded", false);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
        }
    }
}
