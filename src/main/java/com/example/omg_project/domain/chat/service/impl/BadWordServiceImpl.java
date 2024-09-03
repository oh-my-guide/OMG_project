package com.example.omg_project.domain.chat.service.impl;

import com.example.omg_project.domain.chat.entity.BadWord;
import com.example.omg_project.domain.chat.repository.BadWordRepository;
import com.example.omg_project.domain.chat.service.BadWordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class BadWordServiceImpl implements BadWordService {

    private final BadWordRepository badWordRepository;

    @Override
    public String filterMessage(String message) {
        List<BadWord> badWords = badWordRepository.findAll();

        for (BadWord badWord : badWords) {
            // 정규 표현식을 사용하여 비속어 필터링
            if(message.contains(badWord.getWord())) {
                message = message.replaceAll(badWord.getWord(), "삐약삐약");
            }
        }
        return message;
    }
}
