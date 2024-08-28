package com.example.omg_project.domain.user.service.impl;

import com.example.omg_project.domain.user.entity.AdminNotice;
import com.example.omg_project.domain.user.repository.AdminNoticeRepository;
import com.example.omg_project.domain.user.service.AdminNoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminNoticeServiceImpl implements AdminNoticeService {

    private final AdminNoticeRepository omgPosterRepository;

    public void saveNotice(String title, String content) {
        AdminNotice notice = new AdminNotice();
        notice.setTitle(title);
        notice.setContent(content);
        notice.setCreatedAt(LocalDateTime.now());
        omgPosterRepository.save(notice);
    }

    public List<AdminNotice> getAllNotices() {
        return omgPosterRepository.findAll();
    }
}