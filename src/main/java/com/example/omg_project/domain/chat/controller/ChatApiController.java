package com.example.omg_project.domain.chat.controller;
// 필요한 클래스 임포트
import com.example.omg_project.domain.chat.entity.ChatMessage;
import com.example.omg_project.domain.chat.entity.ChatRoom;
import com.example.omg_project.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
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

//    // 특정 채팅방의 메시지 조회 엔드포인트
//    @GetMapping("/rooms/{roomId}/messages")
//    public List<ChatMessage> getMessages(@PathVariable Long roomId) {
//        return chatService.getMessagesByRoomId(roomId);
//    }

    // 새로운 메시지 저장 엔드포인트
    @PostMapping("/messages")
    public ChatMessage postMessage(@RequestBody ChatMessage chatMessage) {
        return chatService.saveMessage(chatMessage);
    }
}