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
import com.mapmory.services.timeline.domain.NotifyTimecapsule;

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
	
	@Value("${timecapsuleTime}")
	private String timecapsuleTime;
	
	@Value("${timeline.coolsms.tophonenumber}")
	private String toPhoneNumber;

	@Value("${timeline.coolsms.url}")
	private String COOL_SMS_URL;
	
	@Value("${timeline.coolsms.apikey}")
	private String INSERT_API_KEY;
	
	@Value("${timeline.coolsms.apisecret}")
	private String INSERT_API_SECRET_KEY;
	
	@Value("${timeline.coolsms.fromphonenumber}")
	private String FROM_PHONE_NUMBER;
	
//	@Test
	@Transactional
	public void testSelectNotifyTimecapsule() throws Exception{
		System.out.println("\n===================================");
		String text="";
		
		for(NotifyTimecapsule n:timelineDao.selectNotifyTimecapsule(LocalTime.parse(timecapsuleTime))) {
			text="";
			text+= n.getUserId()+" 님, 오늘 "+n.getTimecapsulCount()+" 건의 타임캡슐 기록이 존재합니다. ";
			System.out.println(text);
		}
		
		DefaultMessageService messageService=NurigoApp.INSTANCE.initialize(INSERT_API_KEY, INSERT_API_SECRET_KEY, COOL_SMS_URL);
    	
        Message message = new Message();
        // 발신번호 및 수신번호는 반드시 01012345678 형태로 입력되어야 합니다.
        message.setFrom(FROM_PHONE_NUMBER);
        message.setTo(toPhoneNumber.replace("-", ""));
        message.setText(text);

        SingleMessageSentResponse response = messageService.sendOne(new SingleMessageSendingRequest(message));
        System.out.println(response);
        
		Assertions.assertThat(text).isEqualTo("user3 님, 오늘 1 건의 타임캡슐 기록이 존재합니다. ");
		
	}
}
