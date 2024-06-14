package com.mapmory.services.call.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.mapmory.services.call.domain.Message;
import com.mapmory.services.call.service.MessageService;

@Service("messageServiceImpl")
public class MessageServiceImpl implements MessageService{


	 private Map<String, List<Message>> messages = new ConcurrentHashMap<>();

	    @Override
	    public List<Message> getMessagesForUser(String userId) throws Exception {
	        return messages.getOrDefault(userId, new ArrayList<>());
	    }

	    @Override
	    public void addMessage(Message message) throws Exception {
	        messages.computeIfAbsent(message.getToUser(), k -> new ArrayList<>())
	                .add(message);
	    }
}
