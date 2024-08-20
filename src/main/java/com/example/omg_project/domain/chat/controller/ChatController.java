package com.example.omg_project.domain.chat.controller;

import com.example.omg_project.domain.chat.service.ChatService;
import com.example.omg_project.domain.trip.service.TeamService;
import com.example.omg_project.domain.trip.service.TripService;
import com.example.omg_project.domain.user.entity.User;
import com.example.omg_project.domain.user.service.UserService;
import com.example.omg_project.global.jwt.util.JwtTokenizer;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final JwtTokenizer jwtTokenizer;
    private final UserService userService;
    private final TeamService teamService;

    /** TODO
     * 채팅방 화면을 렌더링하는 엔드포인트
     *
     * @param roomId 채팅방 ID
     * @param model  모델 객체, 뷰로 데이터를 전달하는 역할
     * @param request 클라이언트의 요청 정보를 담고 있는 HttpServletRequest 객체
     * @param redirectAttributes 리다이렉트 시에 데이터를 전달하기 위한 객체
     * @return 채팅방 화면을 반환하거나, 예외가 발생할 경우 홈 화면으로 리다이렉트
     */
    @GetMapping("/rooms/{roomId}")
    public String getChatRoom(@PathVariable("roomId") Long roomId, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        try {
            // 쿠키에서 accessToken 찾기
            String accessToken = jwtTokenizer.getAccessTokenFromCookies(request);

            // 사용자 및 채팅방 검증 로직 호출
            // 유효한 accessToken과 채팅방 ID를 기반으로 사용자가 해당 채팅방에 참여하고 있는지 확인
            chatService.validateUserInChatRoom(roomId, accessToken);

            String username = jwtTokenizer.getUsernameFromToken(accessToken);
            User user = userService.findByUsername(username).orElseThrow();
            String usernick = user.getUsernick();

            String tripName = chatService.findTripName(roomId);

            // 검증에 성공하면, 모델에 roomId를 추가하고 채팅방 화면으로 이동
            model.addAttribute("roomId", roomId);
            model.addAttribute("usernick", usernick);
            model.addAttribute("tripName", tripName);
            model.addAttribute("user", user);

            return "chat/chat";  // 채팅 화면으로 이동

        } catch (RuntimeException e) {
            // 예외 발생 시, 에러 메시지를 전달하고 홈 화면으로 리다이렉트
            System.out.println("에러 내용 :::::::::::" + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/";  // 홈 화면으로 리다이렉트
        }
    }
}
