package com.example.omg_project.domain.user.service;

import com.example.omg_project.domain.user.dto.request.AdminNoticeRequest;
import com.example.omg_project.domain.user.entity.AdminNotice;
import java.util.List;

public interface AdminNoticeService {

    void saveNotice(AdminNoticeRequest noticeRequest);

    List<AdminNotice> getAllNotices();
}
