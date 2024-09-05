package com.example.omg_project.domain.user.service.impl;

import com.example.omg_project.domain.user.dto.request.AdminNoticeRequest;
import com.example.omg_project.domain.user.entity.AdminNotice;
import com.example.omg_project.domain.user.repository.AdminNoticeRepository;
import com.example.omg_project.domain.user.service.AdminNoticeService;
import com.example.omg_project.global.exception.CustomException;
import com.example.omg_project.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminNoticeServiceImpl implements AdminNoticeService {

    private final AdminNoticeRepository omgPosterRepository;

    /**
     * 공지사항 작성
     * @param noticeRequest 공지사항 dto
     */
    @Override
    public void saveNotice(AdminNoticeRequest noticeRequest) {
        String title = noticeRequest.getTitle();
        String content = noticeRequest.getContent();

        if (title == null || title.isEmpty() || content == null || content.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_NOTICE_DATA);
        }
        try {
            AdminNotice notice = AdminNotice.builder()
                    .title(title)
                    .content(content)
                    .createdAt(LocalDateTime.now())
                    .build();

            omgPosterRepository.save(notice);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.NOTICE_SAVING_ERROR);
        }
    }

    public List<AdminNotice> getAllNotices() {
        return omgPosterRepository.findAll();
    }
}