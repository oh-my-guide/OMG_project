package com.example.omg_project.domain.user.repository;

import com.example.omg_project.domain.user.entity.AdminNotice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminNoticeRepository extends JpaRepository<AdminNotice, Long> {
}
