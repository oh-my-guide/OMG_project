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

    // Chat Room Errors
    CHAT_ROOM_NOT_FOUND("CHAT_001", "채팅방이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    USER_NOT_IN_CHAT_ROOM("CHAT_002", "사용자가 채팅방에 참여하고 있지 않습니다.", HttpStatus.FORBIDDEN),
    USER_NOT_FOUND("CHAT_003", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_MESSAGE_FORMAT("CHAT_004", "올바른 메시지 형식이 아닙니다.", HttpStatus.BAD_REQUEST),
    TEAM_NOT_FOUND("CHAT_005", "채팅방에 해당하는 팀이 없습니다.", HttpStatus.NOT_FOUND),
    TRIP_NOT_FOUND("CHAT_006", "여행 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // Notification Errors
    JSON_PROCESSING_ERROR("NOTIF_001", "JSON 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    NOTIFICATION_CREATION_ERROR("NOTIF_002", "알림 생성 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    NOTIFICATION_RETRIEVAL_ERROR("NOTIF_003", "알림 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    NOTIFICATION_UPDATE_ERROR("NOTIF_004", "알림 업데이트 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    NOTIFICATION_COUNT_ERROR("NOTIF_005", "알림 개수 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    NOTIFICATION_NOT_FOUND("NOTIF_006", "알림을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    NOTIFICATION_PROCESSING_ERROR("NOTIF_007", "알림 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    DESERIALIZATION_ERROR("NOTIF_008", "데이터 변환 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
