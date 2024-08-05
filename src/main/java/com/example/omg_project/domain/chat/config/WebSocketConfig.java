package com.example.omg_project.domain.chat.config;

import com.example.omg_project.domain.chat.websocket.CustomHandshakeInterceptor;
import com.example.omg_project.domain.chat.websocket.WebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    //security와 함께쓰기위해서 추가
    private final CustomHandshakeInterceptor customHandshakeInterceptor;
    private final WebSocketHandler myWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(myWebSocketHandler, "/chat").setAllowedOrigins("*")
                //security와 함께쓰기위해서 추가
                .addInterceptors(customHandshakeInterceptor);
    }
}