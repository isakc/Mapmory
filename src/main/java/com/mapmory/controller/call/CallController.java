package com.mapmory.controller.call;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.amazonaws.auth.policy.Principal;
import com.mapmory.services.call.domain.CallRequest;
import com.mapmory.services.call.service.CallService;
import org.springframework.security.core.userdetails.User;

//CallController.java
@Controller
@RequestMapping("/video-call")
public class CallController {

 @Autowired
 private CallService callService;

 @GetMapping("/{userId}")
 public String initiateCall(@PathVariable String userId, Model model, @AuthenticationPrincipal User user) throws Exception {
     String currentUser = user.getUsername(); // 인증된 사용자의 아이디 가져오기
     CallRequest callRequest = new CallRequest(currentUser, userId, System.currentTimeMillis());
     callService.requestCall(callRequest);
     model.addAttribute("callRequest", callRequest);
     return "call";
 }
}


