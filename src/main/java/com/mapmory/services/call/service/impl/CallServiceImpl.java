package com.mapmory.services.call.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.mapmory.common.domain.SessionData;
import com.mapmory.common.util.RedisUtil;
import com.mapmory.services.call.domain.CallRequest;
import com.mapmory.services.call.domain.CallResponse;
import com.mapmory.services.call.domain.IceCandidate;
import com.mapmory.services.call.domain.Message;
import com.mapmory.services.call.domain.SdpAnswer;
import com.mapmory.services.call.domain.SdpOffer;
import com.mapmory.services.call.service.CallService;
import com.mapmory.services.call.service.MessageService;
import com.mapmory.services.user.domain.User;
import com.mapmory.services.user.service.UserServiceJM;

@Service("callServiceImpl")
public class CallServiceImpl implements CallService {
	
	@Autowired
	private UserServiceJM userService;

	@Autowired
    private MessageService messageService;
	
	@Autowired
	private RedisUtil<SessionData> redisUtil;
	
	@Autowired
    private SimpMessagingTemplate messagingTemplate;

	@Override
    public CallResponse requestCall(CallRequest callRequest) throws Exception {
        User toUser = userService.findByUserId(callRequest.getToUser());
        Message message = new Message(callRequest.getFromUser(), callRequest.getToUser(), "통화 요청", callRequest.getTimestamp());
        messageService.addMessage(message);
        messagingTemplate.convertAndSendToUser(callRequest.getToUser(), "/queue/incoming-call", callRequest);
        boolean isOnline = redisUtil.select(callRequest.getToUser(), SessionData.class) != null;
        return new CallResponse(toUser.getUserId(), isOnline);
    }

    public SdpAnswer handleOffer(SdpOffer sdpOffer) throws Exception {
        User toUser = userService.findByUserId(sdpOffer.getToUser());
        SdpAnswer sdpAnswer = new SdpAnswer(sdpOffer.getFromUser(), toUser.getUserId(), sdpOffer.getSdp());
        messagingTemplate.convertAndSendToUser(toUser.getUserId(), "/queue/answer", sdpAnswer);
        return sdpAnswer;
    }

    public void handleIceCandidate(IceCandidate iceCandidate) throws Exception {
        User toUser = userService.findByUserId(iceCandidate.getToUser());
        messagingTemplate.convertAndSendToUser(toUser.getUserId(), "/queue/candidate", iceCandidate);
    }


}