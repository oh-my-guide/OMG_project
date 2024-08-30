package com.example.omg_project.domain.user.dto.request;

import jakarta.persistence.Column;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminNoticeRequest {

    private String title;
    private String content;
    private LocalDateTime createdAt;
}
