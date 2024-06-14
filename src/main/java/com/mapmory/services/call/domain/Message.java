package com.mapmory.services.call.domain;

import lombok.Data;

@Data
public class Message {
	
	    private String fromUser;
	    private String toUser;
	    private String content;
	    private long timestamp;

	    public Message(String fromUser, String toUser, String content, long timestamp) {
	        this.fromUser = fromUser;
	        this.toUser = toUser;
	        this.content = content;
	        this.timestamp = timestamp;
	    }
}
