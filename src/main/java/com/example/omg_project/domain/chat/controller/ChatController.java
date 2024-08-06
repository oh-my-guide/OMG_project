package com.example.omg_project.domain.chat.controller;

import com.example.omg_project.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

     // 채팅방 화면을 렌더링하는 엔드포인트
    @GetMapping("/rooms/{roomId}")
    public String getChatRoom(@PathVariable Long roomId, Model model) {
        model.addAttribute("roomId", roomId);   // 채팅방 ID를 모델에 추가하여 타임리프 템플릿으로 전달
        return "chat/chat";
    }
}
