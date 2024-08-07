package com.example.omg_project.domain.chat.websocket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

@Component
public class CustomHandshakeInterceptor extends HttpSessionHandshakeInterceptor {
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        // 인증정보로부터 username을 얻어온다.
        SecurityContext context = SecurityContextHolder.getContext();
        if(context.getAuthentication() != null){
            attributes.put("SPRING_SECURITY_CONTEXT", context);
        }

        // URL 경로에서 roomId 추출
        String path = request.getURI().getPath();
        String roomId = path.split("/")[2];
        attributes.put("roomId", roomId);

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception ex) {
        super.afterHandshake(request, response, wsHandler, ex);
    }
}
