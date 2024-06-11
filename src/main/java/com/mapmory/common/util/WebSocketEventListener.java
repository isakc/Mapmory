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
        String userId = sessionData.getUserId(); // 회원 아이디 가져오기
        
        userService.updateOnlineStatus(userId, true);
    }
    
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) throws Exception {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        HttpServletRequest request = ((ServletServerHttpRequest) headerAccessor.getMessageHeaders().get("simpConnectMessage")).getServletRequest();
        
        SessionData sessionData = redisUtil.getSession(request);
        String userId = sessionData.getUserId(); // 회원 아이디 가져오기
        
        userService.updateOnlineStatus(userId, false);
    }
}