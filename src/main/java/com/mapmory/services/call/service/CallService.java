package com.mapmory.services.call.service;

import com.mapmory.services.call.domain.CallRequest;
import com.mapmory.services.call.domain.CallResponse;
import com.mapmory.services.call.domain.IceCandidate;
import com.mapmory.services.call.domain.SdpAnswer;
import com.mapmory.services.call.domain.SdpOffer;

public interface CallService {
	
	public CallResponse requestCall(CallRequest callRequest) throws Exception;
    
    public SdpAnswer handleOffer(SdpOffer sdpOffer) throws Exception;
    
    public void handleIceCandidate(IceCandidate iceCandidate) throws Exception;
    
    
    
}
