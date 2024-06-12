package com.mapmory.common.util;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.mapmory.common.domain.SessionData;
import com.mapmory.services.user.service.UserServiceJM;

@Component
public class WebSocketEventListener {
    
    @Autowired
    private UserServiceJM userService;
    
    @Autowired
    private RedisUtil<SessionData> redisUtil;
    
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) throws Exception {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        HttpServletRequest request = ((ServletServerHttpRequest) headerAccessor.getMessageHeaders().get("simpConnectMessage")).getServletRequest();
        
        SessionData sessionData = redisUtil.getSession(request);
        String userId = sessionData.getUserId();
        
        // Redis에 세션 정보가 존재하는지 확인 (온라인 상태 판단)
        boolean isOnline = redisUtil.select(userId, SessionData.class) != null;
        System.out.println("WebSocket connected - User: " + userId + ", Online: " + isOnline);
    }
    
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) throws Exception {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        HttpServletRequest request = ((ServletServerHttpRequest) headerAccessor.getMessageHeaders().get("simpConnectMessage")).getServletRequest();
        
        SessionData sessionData = redisUtil.getSession(request);
        String userId = sessionData.getUserId();
        
        // Redis에서 세션 정보 삭제
        redisUtil.delete(userId);
        System.out.println("WebSocket disconnected - User: " + userId);
    }
}