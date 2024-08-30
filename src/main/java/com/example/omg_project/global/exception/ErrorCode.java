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
    DESERIALIZATION_ERROR("NOTIF_008", "데이터 변환 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    //Trip
    USER_NOT_FOUND_EXCEPTION("USER_001", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    CITY_NOT_FOUND_EXCEPTION("CITY_001", "도시를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    TRIP_NOT_FOUND_EXCEPTION("TRIP_001", "여행 일정을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_INVITE_CODE("TEAM_001", "잘못된 초대 코드입니다.", HttpStatus.BAD_REQUEST),
    LEADER_CANNOT_LEAVE_TEAM("TEAM_002", "팀 리더는 팀에서 탈퇴할 수 없습니다.", HttpStatus.BAD_REQUEST),

    // Post
    POST_NOT_FOUND_EXCEPTION("POST_001", "게시글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    COMMENT_NOT_FOUND_EXCEPTION("POST_002", "댓글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    REPLY_NOT_FOUND_EXCEPTION("POST_003", "대댓글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    TRIP_LOCATION_NOT_FOUND_EXCEPTION("POST_004", "장소를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_SEARCH_OPTION_EXCEPTION("SEARCH_001", "검색 옵션이 유효하지 않습니다.", HttpStatus.BAD_REQUEST),
    TEAM_NOT_FOUND_EXCEPTION("TEAM_003", "채팅방 ID에 해당하는 팀을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);


    private final String code;
    private final String message;
    private final HttpStatus status;
}
