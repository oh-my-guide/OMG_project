package com.example.omg_project.global.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
public class CustomResponse<T> {
    private HttpStatus status;
    private String message;
    private T data;

    // 헤더 포함해서 성공메시지만 전달할 경우
    public CustomResponse(String message) {
        this.message = message;
    }

    // 헤더 포함해서 성공메시지 및 필요한 Data 같이 전달할 경우
    public CustomResponse(String message, T data) {
        this.message = message;
        this.data = data;
    }

    public CustomResponse(HttpStatus status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}