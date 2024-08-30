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
    PUT_OBJECT_EXCEPTION("S3_006", "S3에 객체를 업로드하는 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    // User
    USER_NOT_FOUND("USER_001", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    USER_DELETION_ERROR("USER_002", "삭제할 사용자가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    TEMP_PASSWORD_SENDING_ERROR("USER_003", "임시 비밀번호 전송 오류입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    EMAIL_CREATION_ERROR("USER_004", "이메일 생성 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_CREDENTIALS("USER_005", "자격 증명이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED),
    USER_SIGNUP_ERROR("USER_006", "회원가입 중 오류가 발생했습니다.", HttpStatus.BAD_REQUEST),
    TOKEN_DELETION_ERROR("USER_007", "토큰 삭제 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    ROLE_NOT_FOUND("USER_008", "User 역할이 없습니다.", HttpStatus.NOT_FOUND),
    UNSUPPORTED_LOGIN_PROVIDER("USER_009","지원하지 않는 로그인 제공자입니다.", HttpStatus.BAD_REQUEST),
    TOKEN_NOT_FOUND("USER_009", "엑세스 토큰이 없습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_NOTICE_DATA("NOTICE_001", "제목 또는 내용이 비어 있습니다.", HttpStatus.BAD_REQUEST),
    NOTICE_SAVING_ERROR("NOTICE_002", "공지사항 저장 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
