package com.example.omg_project.domain.user.service;

import com.example.omg_project.domain.user.entity.AdminNotice;
import java.util.List;

public interface AdminNoticeService {

    void saveNotice(String title, String content);

    List<AdminNotice> getAllNotices();
}
