package com.mapmory.unit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import com.mapmory.services.user.service.impl.UserServiceScheduler;

@SpringBootTest
public class UserServiceSchedularTest {

	@Autowired
	@Qualifier("userServiceScheduler")
	private UserServiceScheduler scheduler;
	
	
	@Test
	public void testFetchTermsAndConditions() throws Exception {
		
		scheduler.fetchTermsAndConditions();
	}
	
	
}
