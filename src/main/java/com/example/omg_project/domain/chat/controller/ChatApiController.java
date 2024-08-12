package com.example.omg_project.domain.chat.controller;

import com.example.omg_project.domain.chat.dto.ChatMessageDTO;
import com.example.omg_project.domain.chat.entity.ChatRoom;
import com.example.omg_project.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 채팅 API 컨트롤러 클래스
 * RESTful API 엔드포인트를 정의하여 클라이언트가 채팅 관련 기능을 사용할 수 있도록 함
 */
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatApiController {

    private final ChatService chatService;

    /**
     * 특정 채팅방의 메시지를 조회하는 엔드포인트
     * HTTP GET 요청을 받아 해당 채팅방의 메시지를 조회하고, 메시지 목록을 반환
     *
     * @param roomId 조회할 채팅방의 ID
     * @return 채팅방의 메시지 목록(ChatMessageDTO 객체의 리스트)
     */
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<List<ChatMessageDTO>> getMessages(@PathVariable Long roomId) {
        List<ChatMessageDTO> messages = chatService.getMessagesByRoomId(roomId);
        return ResponseEntity.ok(messages);
    }
}