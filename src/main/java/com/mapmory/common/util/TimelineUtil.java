package com.mapmory.common.util;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mapmory.services.timeline.domain.ImageTag;
import com.mapmory.services.timeline.domain.Record;
import com.mapmory.services.timeline.domain.SharedRecord;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;

@Component("timelineUtil")
public class TimelineUtil {

	@Value("${timeline.coolsms.url}")
	private String COOL_SMS_URL;
	
	@Value("${timeline.coolsms.apikey}")
	private String INSERT_API_KEY;
	
	@Value("${timeline.coolsms.apisecret}")
	private String INSERT_API_SECRET_KEY;
	
	@Value("${timeline.coolsms.fromphonenumber}")
	private String FROM_PHONE_NUMBER;
	
	//map을 record로 묶어주는 기능
		public static Record mapToRecord(Map<String, Object> map) {
			List<Map<String,Object>> imageTagList =(List<Map<String,Object>>)map.get("imageTagList");
			
			Record record=Record.builder()
					.recordNo((int)map.get("recordNo"))
					.recordUserId((String)map.get("recordUserId"))
					.recordTitle((String)map.get("recordTitle"))
					.latitude((Double)map.get("latitude"))
					.longitude((Double)map.get("longitude"))
					.checkpointAddress((String)map.get("checkpointAddress"))
					.checkpointDate((String)map.get("checkpointDate"))
					.mediaName(map.get("mediaName") ==null ? "" : (String)map.get("mediaName"))
					.imageName(listToImage(imageTagList))
					.hashtag(listToHashtag(imageTagList))
					.categoryNo((Integer)map.get("categoryNo"))
					.recordText(map.get("recordText") ==null ? "" : (String)map.get("recordText"))
					.tempType((Integer)map.get("tempType"))
					.recordAddDate((String)map.get("recordAddDate"))
					.sharedDate((String)map.get("sharedDate"))
					.updateCount((Integer)map.get("updateCount"))
					.d_DayDate((Date)map.get("d_DayDate"))
					.timecapsuleType((Integer)map.get("timecapsuleType"))
					.build();
			return record;
		}
		
		public static SharedRecord mapToSharedRecord(Map<String, Object> map) {
			List<Map<String,Object>> imageTagList =(List<Map<String,Object>>)map.get("imageTagList");
			
			SharedRecord sharedRecord=SharedRecord.builder()
					.recordNo((int)map.get("recordNo"))
					.recordUserId((String)map.get("recordUserId"))
					.recordTitle((String)map.get("recordTitle"))
					.latitude((Double)map.get("latitude"))
					.longitude((Double)map.get("longitude"))
					.checkpointAddress((String)map.get("checkpointAddress"))
					.checkpointDate((String)map.get("checkpointDate"))
					.mediaName(map.get("mediaName") ==null ? "" : (String)map.get("mediaName"))
					.imageName(listToImage(imageTagList))
					.hashtag(listToHashtag(imageTagList))
					.categoryNo((Integer)map.get("categoryNo"))
					.recordText(map.get("recordText") ==null ? "" : (String)map.get("recordText"))
					.tempType((Integer)map.get("tempType"))
					.recordAddDate((String)map.get("recordAddDate"))
					.sharedDate((String)map.get("sharedDate"))
					.updateCount((Integer)map.get("updateCount"))
					.d_DayDate((Date)map.get("d_DayDate"))
					.timecapsuleType((Integer)map.get("timecapsuleType"))
					.nickname((String)map.get("nickname"))
					.profileImageName((String)map.get("profileImageName"))
					.subscriptionEndDate((long)map.get("subscriptionEndDate"))
					.categoryName((String)map.get("categoryName"))
					.categoryImoji((String)map.get("categoryImoji"))
					.replyCount((long)map.get("replyCount"))
					.logsCount((long)map.get("logsCount"))
					.build();
			return sharedRecord;
		}
		
		public static List<ImageTag> listToImage (List<Map<String, Object>> imageTagList) {
			List<ImageTag> imageName = new ArrayList<ImageTag>();
			for (Map<String, Object> map : imageTagList) {
		        if ((int) map.get("imageTagType") == 1) {
		            imageName.add(ImageTag.builder()
		                    .imageTagOrder((int) map.get("imageTagOrder"))
		                    .imageTagType((int) map.get("imageTagType"))
		                    .imageTagText((String) map.get("imageTagText"))
		                    .build());
		        }
		    }
			return imageName;
		}
		
		public static List<ImageTag> listToHashtag (List<Map<String, Object>> imageTagList) {
			List<ImageTag> hashtag = new ArrayList<ImageTag>();
			for (Map<String, Object> map : imageTagList) {
				if ((int) map.get("imageTagType") == 0) {
		            hashtag.add(ImageTag.builder()
		                    .imageTagOrder((int) map.get("imageTagOrder"))
		                    .imageTagType((int) map.get("imageTagType"))
		                    .imageTagText((String) map.get("imageTagText"))
		                    .build());
		        }
		    }
			return hashtag;
		}
		
		public static List<Map<String, Object>> imageTagToList (List<ImageTag> imageName, List<ImageTag> hashtag) {
			List<Map<String,Object>> imageTagList = new ArrayList<Map<String,Object>>();
		    // 이미지 태그가 있는 경우에만 처리
		    if (imageName != null) {
		        for (ImageTag i : imageName) {
		            Map<String,Object> map = new HashMap<String, Object>();
		            map.put("imageTagType", 1);  // 이미지 태그의 타입을 1로 설정
		            map.put("imageTagText", i.getImageTagText());
		            imageTagList.add(map);  // tempList에 이미지 태그 정보 추가
		        }
		    }
		    
		    // 해시태그가 있는 경우에만 처리
		    if (hashtag != null) {
		        for (ImageTag h : hashtag) {
		            Map<String,Object> map = new HashMap<String, Object>();
		            map.put("imageTagType", 0);  // 해시태그의 타입을 0으로 설정
		            map.put("imageTagText", h.getImageTagText());
		            imageTagList.add(map);  // tempList에 해시태그 정보 추가
		        }
		    }
		    
		    return imageTagList;
		}
		
	    public SingleMessageSentResponse sendOne(String toPhoneNumber, String text) {
	    	System.out.println("INSERT_API_KEY : " + INSERT_API_KEY);
	    	System.out.println("INSERT_API_SECRET_KEY : " + INSERT_API_SECRET_KEY);
	    	System.out.println("COOL_SMS_URL : " + COOL_SMS_URL);
	    	System.out.println("FROM_PHONE_NUMBER : " + FROM_PHONE_NUMBER);
//	    	DefaultMessageService messageService=NurigoApp.INSTANCE.initialize(INSERT_API_KEY, INSERT_API_SECRET_KEY, COOL_SMS_URL);
//	    	
//	        Message message = new Message();
//	        // 발신번호 및 수신번호는 반드시 01012345678 형태로 입력되어야 합니다.
//	        message.setFrom(FROM_PHONE_NUMBER);
//	        message.setTo(toPhoneNumber.replace("-", ""));
//	        message.setText(text);
//
//	        SingleMessageSentResponse response = messageService.sendOne(new SingleMessageSendingRequest(message));
//	        System.out.println(response);
//
//	        return response;
	    	return null;
	    }

}
