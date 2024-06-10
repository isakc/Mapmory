package com.mapmory.common.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.mapmory.services.user.service.UserServiceJM;


@Component
public class WebSocketEventListener {
    
	@Autowired
    private UserServiceJM userService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) throws Exception{
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String userId = (String) headerAccessor.getSessionAttributes().get("userId");

        userService.updateOnlineStatus(userId, true);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) throws Exception{
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String userId = (String) headerAccessor.getSessionAttributes().get("userId");

        userService.updateOnlineStatus(userId, false);
    }
}
