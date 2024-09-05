package com.example.omg_project.domain.chat.websocket;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

@Component
public class CustomHandshakeInterceptor extends HttpSessionHandshakeInterceptor {

    /**
     * 핸드쉐이크 전에 호출되어 WebSocket 연결을 설정하는 메서드입니다.
     *
     * @param request WebSocket 요청 객체
     * @param response WebSocket 응답 객체
     * @param wsHandler WebSocket 핸들러
     * @param attributes 핸드쉐이크 속성
     * @return boolean 핸드쉐이크 성공 여부
     * @throws Exception 핸드쉐이크 중 발생할 수 있는 예외
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {

        // 쿠키에서 accessToken 추출
        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
            String token = extractTokenFromCookies(servletRequest);
            if (token != null) {
                attributes.put("jwtToken", token);  // 추출한 토큰을 핸드쉐이크 속성에 추가
            }
        }

        // URL 경로에서 roomId 추출
        String path = request.getURI().getPath();
        String roomId = path.split("/")[2];
        attributes.put("roomId", roomId);  // 추출한 roomId를 핸드쉐이크 속성에 추가

        return true;  // 핸드쉐이크 성공
    }

    /**
     * 쿠키에서 accessToken을 추출하는 메서드입니다.
     *
     * @param request HTTP 요청 객체
     * @return String accessToken 쿠키 값, 없으면 null
     */
    private String extractTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();  // accessToken 쿠키 값 반환
                }
            }
        }
        return null;  // accessToken 쿠키가 없으면 null 반환
    }

    /**
     * 핸드쉐이크 후에 호출되는 메서드입니다.
     *
     * @param request WebSocket 요청 객체
     * @param response WebSocket 응답 객체
     * @param wsHandler WebSocket 핸들러
     * @param ex 핸드쉐이크 중 발생한 예외
     */
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception ex) {
        super.afterHandshake(request, response, wsHandler, ex);  // 부모 클래스의 afterHandshake 호출
    }
}
