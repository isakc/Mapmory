package com.mapmory.controller.chatting;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/chat/*")
public class ChattingRestController {
	
	@Value("${mongoURL}")
	String mongoURL;
	
	public ChattingRestController() {
		System.out.println(this.getClass());
	}
	
	//mongoDB 접속 String 전달
	@GetMapping(value="json/getMongo")
	public String getMongo() throws Exception {	
		return mongoURL;
	}
	
}
