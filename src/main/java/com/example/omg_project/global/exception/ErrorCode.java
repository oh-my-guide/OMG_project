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

    //Trip
    USER_NOT_FOUND_EXCEPTION("USER_001", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    CITY_NOT_FOUND_EXCEPTION("CITY_001", "도시를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    TRIP_NOT_FOUND_EXCEPTION("TRIP_001", "여행 일정을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_INVITE_CODE("TEAM_001", "잘못된 초대 코드입니다.", HttpStatus.BAD_REQUEST),
    LEADER_CANNOT_LEAVE_TEAM("TEAM_002", "팀 리더는 팀에서 탈퇴할 수 없습니다.", HttpStatus.BAD_REQUEST),
    TEAM_NOT_FOUND_EXCEPTION("TEAM_003", "채팅방 ID에 해당하는 팀을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),;

    private final String code;
    private final String message;
    private final HttpStatus status;
}
