package com.example.omg_project.domain.chat.service.impl;

import com.example.omg_project.domain.chat.dto.ChatMessageDTO;
import com.example.omg_project.domain.chat.entity.ChatMessage;
import com.example.omg_project.domain.chat.entity.ChatRoom;
import com.example.omg_project.domain.chat.repository.ChatMessageRepository;
import com.example.omg_project.domain.chat.repository.ChatRoomRepository;
import com.example.omg_project.domain.chat.service.ChatService;
import com.example.omg_project.domain.trip.entity.Team;
import com.example.omg_project.domain.trip.service.TeamService;
import com.example.omg_project.domain.user.entity.User;
import com.example.omg_project.domain.user.service.UserService;
import com.example.omg_project.global.jwt.util.JwtTokenizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 채팅 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final TeamService teamService;

    /**
     * 채팅방의 존재 여부를 확인하는 메서드
     *
     * @param roomId 채팅방 ID
     * @return 채팅방이 존재하면 true, 그렇지 않으면 false
     */
    @Override
    public boolean existsById(Long roomId) {
        return chatRoomRepository.existsById(roomId);
    }

    /**
     * 해당 채팅방의 모든 메시지를 ChatMessageDTO로 변환하여 반환하는 메서드
     *
     * @param roomId 채팅방 ID
     * @return 채팅방에 속한 메시지 목록을 DTO로 변환하여 반환
     */
    @Override
    public List<ChatMessageDTO> getMessagesByRoomId(Long roomId) {
        // 채팅방 ID에 해당하는 메시지를 가져옴
        List<ChatMessage> chatMessages = chatMessageRepository.findByChatRoomId(roomId);

        // 메시지 엔티티를 DTO로 변환하여 반환
        return chatMessages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * ChatMessage 엔티티를 ChatMessageDTO로 변환하는 메서드
     *
     * @param chatMessage 채팅 메시지 엔티티
     * @return 변환된 ChatMessageDTO 객체
     */
    @Override
    public ChatMessageDTO convertToDTO(ChatMessage chatMessage) {
        // 엔티티의 각 필드를 DTO로 매핑
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setId(chatMessage.getId());
        dto.setUserNickname(chatMessage.getUserNickname());
        dto.setMessage(chatMessage.getMessage());
        dto.setCreatedAt(chatMessage.getCreatedAt().toString());
        return dto;
    }

    /**
     * 사용자와 채팅방의 접근 권한을 검증하는 메서드
     *
     * @param roomId 채팅방 ID
     * @param user 사용자 객체
     */
    @Override
    public void validateUserInChatRoom(Long roomId, User user) {
        // 채팅방이 존재하지 않을 경우 예외 발생
        if (!existsById(roomId)) {
            throw new RuntimeException("채팅방이 존재하지 않습니다.");
        }

        // 채팅방에 연결된 팀 정보를 조회하고, 사용자가 팀에 속해 있는지 확인
        Team team = teamService.findByChatRoomId(roomId);
        if (!team.getUsers().contains(user)) {
            // 사용자가 팀에 속해 있지 않으면 예외 발생
            throw new RuntimeException("사용자가 채팅방에 참여하고 있지 않습니다.");
        }
    }

    /**
     * 주어진 채팅방 ID를 사용하여 팀을 찾고,
     * 해당 팀에 연관된 여행의 이름을 반환하는 메서드.
     *
     * @param roomId 채팅방 ID
     * @return 여행 이름
     */
    @Override
    public String findTripName(Long roomId) {
        Team team = teamService.findByChatRoomId(roomId);
        return team.getTrip().getTripName();
    }
}
