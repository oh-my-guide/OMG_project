package com.example.omg_project.domain.chat.repository;

import com.example.omg_project.domain.chat.entity.BadWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BadWordRepository extends JpaRepository<BadWord, Long> {
}
