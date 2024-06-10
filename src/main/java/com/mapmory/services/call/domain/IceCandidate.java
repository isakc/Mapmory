package com.mapmory.services.call.domain;

import lombok.Data;

@Data
public class IceCandidate {
	private String fromUser;
    private String toUser;
    private String candidate;
}
