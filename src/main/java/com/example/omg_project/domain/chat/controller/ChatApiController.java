package com.example.omg_project.domain.chat.controller;

import com.example.omg_project.domain.chat.dto.ChatMessageDTO;
import com.example.omg_project.domain.chat.entity.ChatRoom;
import com.example.omg_project.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// REST 컨트롤러로 설정
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatApiController {

    // ChatService 주입
    private final ChatService chatService;

    // 새로운 채팅방 생성 엔드포인트
    @PostMapping("/rooms")
    public ChatRoom createRoom() {
        return chatService.createRoom();
    }

    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<List<ChatMessageDTO>> getMessages(@PathVariable Long roomId) {
        List<ChatMessageDTO> messages = chatService.getMessagesByRoomId(roomId);
        return ResponseEntity.ok(messages);
    }
}