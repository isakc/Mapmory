package com.mapmory.common.util;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mapmory.services.timeline.domain.ImageTag;
import com.mapmory.services.timeline.domain.Record;

public class TimelineUtil {
	//map을 record로 묶어주는 기능
		public static Record recordToMap(Map<String, Object> map) {
			List<Map<String,Object>> imageTagList =(List<Map<String,Object>>)map.get("imageTagList");
			
			Record record=Record.builder()
					.recordNo((int)map.get("recordNo"))
					.recordUserId((String)map.get("recordUserId"))
					.recordTitle((String)map.get("recordTitle"))
					.latitude((Double)map.get("latitude"))
					.longitude((Double)map.get("longitude"))
					.checkpointAddress((String)map.get("checkpointAddress"))
					.checkpointDate((LocalDateTime)map.get("checkpointDate"))
					.mediaName(map.get("mediaName") ==null ? "" : (String)map.get("mediaName"))
					.imageName(imageToList(imageTagList))
					.hashtag(hashtagToList(imageTagList))
					.categoryNo((Integer)map.get("categoryNo"))
					.recordText(map.get("recordText") ==null ? "" : (String)map.get("recordText"))
					.tempType((Integer)map.get("tempType"))
					.recordAddDate((LocalDateTime)map.get("recordAddDate"))
					.sharedDate((LocalDateTime)map.get("sharedDate"))
					.updateCount((Integer)map.get("updateCount"))
					.d_DayDate((Date)map.get("d_DayDate"))
					.timecapsuleType((Integer)map.get("timecapsuleType"))
					.build();
			return record;
		}
		
		public static List<ImageTag> imageToList (List<Map<String, Object>> imageTagList) {
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
		
		public static List<ImageTag> hashtagToList (List<Map<String, Object>> imageTagList) {
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
		
		public static List<Map<String, Object>> listToImageTag (List<ImageTag> imageName, List<ImageTag> hashtag) {
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

}
