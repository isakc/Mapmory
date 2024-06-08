package com.mapmory.unit.timeline;

import java.time.LocalTime;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.mapmory.common.util.TimelineUtil;
import com.mapmory.services.timeline.dao.TimelineDao;
import com.mapmory.services.timeline.dto.NotifyTimecapsuleDto;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;

@SpringBootTest
public class TimelineDaoTest {

	@Autowired
	@Qualifier("timelineDao")
	private TimelineDao timelineDao;
	
	@Autowired
	@Qualifier("timelineUtil")
	private TimelineUtil timelineUtil;
	
	@Value("${timecapsule.time}")
	String timecapsuleTime;
	
	@Value("${timeline.coolsms.tophonenumber}")
	private String toPhoneNumber;
	
	@Test
	@Transactional
	public void testSelectNotifyTimecapsule() throws Exception{
		System.out.println("\n===================================");
		String text="";
		
		for(NotifyTimecapsuleDto n:timelineDao.selectNotifyTimecapsule(LocalTime.parse(timecapsuleTime))) {
			text="";
			text+= n.getUserId()+" 님, 오늘 "+n.getTimecapsulCount()+" 건의 타임캡슐 기록이 존재합니다. ";
			
			timelineUtil.sendOne(n.getUserPhoneNumber(), text);
		}
        
		Assertions.assertThat(text).isEqualTo("user3 님, 오늘 1 건의 타임캡슐 기록이 존재합니다. ");
		
	}
}
