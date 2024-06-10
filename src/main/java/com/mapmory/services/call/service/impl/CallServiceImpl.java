package com.mapmory.services.call.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.mapmory.services.call.domain.CallRequest;
import com.mapmory.services.call.domain.CallResponse;
import com.mapmory.services.call.domain.IceCandidate;
import com.mapmory.services.call.domain.SdpAnswer;
import com.mapmory.services.call.domain.SdpOffer;
import com.mapmory.services.call.domain.UserJM;
import com.mapmory.services.call.service.CallService;
import com.mapmory.services.user.service.UserServiceJM;

@Service("callServiceImpl")
public class CallServiceImpl implements CallService {
	
	@Autowired
	UserServiceJM userService;

	@Autowired
    private SimpMessagingTemplate messagingTemplate;

	public CallResponse requestCall(String fromUserId, String toUserId) throws Exception {
        UserJM toUser = userService.findByUserId(toUserId);
        CallRequest callRequest = new CallRequest(fromUserId, toUser.getUserId(), System.currentTimeMillis());
        messagingTemplate.convertAndSendToUser(toUser.getUserId(), "/queue/call", callRequest);

        return new CallResponse(toUser.getUserId(), toUser.isOnline());
    }

    public SdpAnswer handleOffer(SdpOffer sdpOffer) throws Exception{
    	UserJM toUser = userService.findByUserId(sdpOffer.getToUser());
        SdpAnswer sdpAnswer = new SdpAnswer(
                sdpOffer.getFromUser(),
                toUser.getUserId(),
                sdpOffer.getSdp()
        );
        messagingTemplate.convertAndSendToUser(toUser.getUserId(), "/queue/answer", sdpAnswer);

        return sdpAnswer;
    }

    public void handleIceCandidate(IceCandidate iceCandidate) throws Exception{
    	UserJM toUser = userService.findByUserId(iceCandidate.getToUser());
        messagingTemplate.convertAndSendToUser(toUser.getUserId(), "/queue/candidate", iceCandidate);
    }
}