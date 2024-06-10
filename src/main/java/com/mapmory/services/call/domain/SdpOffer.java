package com.mapmory.services.call.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class SdpOffer {
	private String fromUser;
    private String toUser;
    private String sdp;

}
