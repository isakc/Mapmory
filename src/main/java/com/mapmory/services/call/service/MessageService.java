package com.mapmory.services.call.service;

import java.util.List;

import com.mapmory.services.call.domain.Message;

public interface MessageService {
	
	public List<Message> getMessagesForUser(String userId) throws Exception;
	
	public void addMessage(Message message) throws Exception;

}
