package com.mapmory.services.timeline.service;

import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.mapmory.common.util.TimelineUtil;
import com.mapmory.services.timeline.dao.TimelineDao;
import com.mapmory.services.timeline.domain.Record;
import com.mapmory.services.timeline.dto.NotifyTimecapsuleDto;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;

@Component
public class TimelineScheduler {
	
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
	
//	0 0/5 * * * ? : 매 5분마다 실행
//			0 0 0/1 * * ? : 매 1시간마다 실행
//			0 0 12 * * ? : 매일 낮 12시에
//			0 15 10 ? * * : 매일 오전 10:15분에 실행
//			0 15 10 * * ? : 매일 오전 10:15분에 실행
//			0 * 14 * * ? : 매일 오후 2:00에 시작해서 매분마다 실행하고 2:59분에 종료
//			0 0/5 14,18 * * ? : 매일 오후 2:00에 시작해서 5분마다 실행되어 2:55에 끝나고, 6:00에 시작하여 5분마다 실행되어 6:55에 종료
//			0 0-5 14 * * ? : 매일 오후 2:00에 시작하여 매분마다 실행하고 오후 2:05분에 종료
//			0 10,44 14 ? 3 WED : 3월 동안 오후 2:10과 2:44 실행
//			0 15 10 ? * MON-FRI : 주중 오전 10:15분에
//			0 15 10 15 * ? : 매달 15일 오전 10:15에
//			0 15 10 L * ? : 매월 말일 오전 10:15에
//			0 15 10 ? * 6L : 매월 마지막 금요일 오전 10:15에
//			0 15 10 ? * 6#3 : 매월 3째주 금요일 오전 10:15에
	//스케줄러
//	@Scheduled(cron="0 20 20 * * *")
	public void Scheduler() throws Exception {
		String text="";
		
		for(NotifyTimecapsuleDto n:timelineDao.selectNotifyTimecapsule(LocalTime.parse(timecapsuleTime))) {
			text="";
			text+= n.getUserId()+" 님, 오늘 "+n.getTimecapsulCount()+" 건의 타임캡슐 기록이 존재합니다. ";
			
			timelineUtil.sendOne(n.getUserPhoneNumber(), text);
		}
		//cool sms api 문자보내기
//		DefaultMessageService messageService=NurigoApp.INSTANCE.initialize(INSERT_API_KEY, INSERT_API_SECRET_KEY, COOL_SMS_URL);
//    	
//        Message message = new Message();
//        // 발신번호 및 수신번호는 반드시 01012345678 형태로 입력되어야 합니다.
//        message.setFrom(FROM_PHONE_NUMBER);
//        message.setTo(toPhoneNumber.replace("-", ""));
//        message.setText(text);
//
//        SingleMessageSentResponse response = messageService.sendOne(new SingleMessageSendingRequest(message));
//        System.out.println(response);
	}

}
