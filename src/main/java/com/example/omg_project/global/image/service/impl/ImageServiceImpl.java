package com.example.omg_project.global.image.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import com.example.omg_project.global.exception.CustomException;
import com.example.omg_project.global.exception.ErrorCode;
import com.example.omg_project.global.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageServiceImpl implements ImageService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    /**
     * 외부에서 사용할 public 메서드
     * 업로드할 이미지를 S3에 업로드하고 URL을 반환
     * @param image 업로드할 이미지 파일
     * @return S3에 저장된 이미지 객체의 S3 URL
     * @throws CustomException 파일이 비어있거나 이름이 없는 경우
     */
    @Override
    public String upload(MultipartFile image) {
        if (image.isEmpty() || Objects.isNull(image.getOriginalFilename())) {
            throw new CustomException(ErrorCode.EMPTY_FILE_EXCEPTION);
        }
        return uploadImage(image);
    }

    /**
     * 1. validateImageFileExtention()을 호출하여 확장자 명이 올바른지 확인
     * 2. uploadImageToS3()를 호출하여 이미지를 S3에 업로드
     * @param image 업로드할 이미지 파일
     * @return S3에 저장된 이미지의 S3 URL
     * @throws CustomException 이미지 업로드 중 IOException 발생한 경우
     */
    @Override
    public String uploadImage(MultipartFile image) {
        validateImageFileExtention(image.getOriginalFilename());
        try {
            return uploadImageToS3(image);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.IO_EXCEPTION_ON_IMAGE_UPLOAD);
        }
    }

    /**
     * filename을 받아서 이미지 파일 확장자 검증
     * @param filename 검증할 파일 이름
     * @throws CustomException 유효하지 않은 확장자일 경우
     */
    @Override
    public void validateImageFileExtention(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            throw new CustomException(ErrorCode.NO_FILE_EXTENTION);
        }

        String extension = filename.substring(lastDotIndex + 1).toLowerCase();
        List<String> allowedExtentionList = Arrays.asList("jpg", "jpeg", "png", "gif");

        if (!allowedExtentionList.contains(extension)) {
            throw new CustomException(ErrorCode.INVALID_FILE_EXTENTION);
        }
    }

    /**
     * 이미지 파일을 S3에 업로드하고 URL을 반환
     * @param image 업로드할 이미지 파일
     * @return 업로드된 이미지의 S3 URL
     * @throws IOException
     * @throws CustomException
     */
    @Override
    public String uploadImageToS3(MultipartFile image) throws IOException {
        String originalFilename = image.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        String s3FileName = UUID.randomUUID().toString().substring(0, 10) + originalFilename;

        // 입력 스트림을 통해 파일 데이터 읽어옴
        InputStream is = image.getInputStream();
        // 파일 데이터를 바이트 배열로 변환
        byte[] bytes = IOUtils.toByteArray(is);

        // 파일 메타데이터 설정
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("image/" + extension);
        metadata.setContentLength(bytes.length);

        // 바이트 배영를 입력 스트림으로 변환
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

        try {
            // S3에 업로드 요청을 생성하고 파일을 업로드
            PutObjectRequest putObjectRequest =
                    new PutObjectRequest(bucketName, s3FileName, byteArrayInputStream, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead);
            amazonS3.putObject(putObjectRequest);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.PUT_OBJECT_EXCEPTION);
        } finally {
            byteArrayInputStream.close();
            is.close();
        }

        return amazonS3.getUrl(bucketName, s3FileName).toString();
    }
}
