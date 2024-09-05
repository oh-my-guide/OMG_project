package com.example.omg_project.domain.chat.service.impl;

import com.example.omg_project.domain.chat.entity.BadWord;
import com.example.omg_project.domain.chat.repository.BadWordRepository;
import com.example.omg_project.domain.chat.service.BadWordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BadWordServiceImpl implements BadWordService {

    private final BadWordRepository badWordRepository;

    /**
     * 주어진 메시지에서 비속어를 필터링하여 반환
     *
     * @param message 필터링할 메시지
     * @return 비속어가 필터링된 메시지
     */
    @Override
    public String filterMessage(String message) {
        List<BadWord> badWords = badWordRepository.findAll();  // 비속어 목록 조회

        for (BadWord badWord : badWords) {
            if(message.contains(badWord.getWord())) {
                message = message.replaceAll(badWord.getWord(), "삐약삐약");
            }
        }
        return message;  // 필터링된 메시지 반환
    }
}
