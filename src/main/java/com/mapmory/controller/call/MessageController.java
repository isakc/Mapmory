package com.mapmory.controller.call;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.mapmory.services.call.domain.Message;
import com.mapmory.services.call.service.MessageService;
import com.mapmory.services.user.domain.User;

@Controller
public class MessageController {
	
    @Autowired
    private MessageService messageService;

    @GetMapping("message")
    public String getMessages(Model model, @AuthenticationPrincipal User user) throws Exception{
        String userId = user.getUserId(); // 사용자 아이디 가져오기
        List<Message> messages = messageService.getMessagesForUser(userId);
        model.addAttribute("messages", messages);
        return "messaging";
    }
}