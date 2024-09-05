package com.example.omg_project.domain.user.dto.request;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 관리자 공지사항 dto
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminNoticeRequest {

    private String title;
    private String content;
    private LocalDateTime createdAt;
}
