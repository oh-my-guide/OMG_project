package com.example.omg_project.global.exception;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // AWS S3
    EMPTY_FILE_EXCEPTION("S3_001", "파일이 비어 있습니다.", HttpStatus.BAD_REQUEST),
    NO_FILE_EXTENTION("S3_002", "파일 확장자가 없습니다.", HttpStatus.BAD_REQUEST),
    INVALID_FILE_EXTENTION("S3_003", "유효하지 않은 파일 확장자입니다.", HttpStatus.BAD_REQUEST),
    IO_EXCEPTION_ON_IMAGE_UPLOAD("S3_004", "이미지 업로드 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    IO_EXCEPTION_ON_IMAGE_DELETE("S3_005", "이미지 삭제 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    PUT_OBJECT_EXCEPTION("S3_006", "S3에 객체를 업로드하는 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),;

    private final String code;
    private final String message;
    private final HttpStatus status;
}
