package com.mapmory.controller.call;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mapmory.services.call.domain.CallResponse;
import com.mapmory.services.call.domain.IceCandidate;
import com.mapmory.services.call.domain.SdpAnswer;
import com.mapmory.services.call.domain.SdpOffer;
import com.mapmory.services.call.service.CallService;

@RestController
@RequestMapping("/call/*")
@CrossOrigin(origins = "http://192.168.0.22:3000")
public class CallRestController {

	@Autowired
	@Qualifier("callServiceImpl")
	private CallService callService;
	
	@PostMapping
    public ResponseEntity<CallResponse> requestCall(@RequestParam("toUserId") String toUserId) throws Exception {
        String fromUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        CallResponse callResponse = callService.requestCall(fromUsername, toUserId);
        return ResponseEntity.ok(callResponse);
    }



    @PostMapping("/offer")
    public ResponseEntity<SdpAnswer> handleOffer(@RequestBody SdpOffer sdpOffer) throws Exception{
        SdpAnswer sdpAnswer = callService.handleOffer(sdpOffer);
        return ResponseEntity.ok(sdpAnswer);
    }



    @PostMapping("/candidate")
    public ResponseEntity<Void> handleIceCandidate(@RequestBody IceCandidate iceCandidate) throws Exception{
        callService.handleIceCandidate(iceCandidate);
        return ResponseEntity.ok().build();
    }

}