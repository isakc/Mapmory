package com.mapmory.common.util;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.mapmory.services.timeline.domain.ImageTag;
import com.mapmory.services.timeline.domain.Record;

public class TimelineUtil {
	//map을 record로 묶어주는 기능
		public static Record recordToMap(Map<String, Object> map) throws Exception {
			Record record=Record.builder()
					.recordNo((int)map.get("recordNo"))
					.recordUserId((String)map.get("recordUserId"))
					.recordTitle((String)map.get("recordTitle"))
					.latitude((Double)map.get("latitude"))
					.longitude((Double)map.get("longitude"))
					.checkpointAddress((String)map.get("checkpointAddress"))
					.checkpointDate((LocalDateTime)map.get("checkpointDate"))
					.mediaName(map.get("mediaName") ==null ? "" : (String)map.get("mediaName"))
					.imageTag((List<Map<String,Object>>)map.get("imageTag"))
//					.imageName((List<String>)map.get("imageName"))
//					.hashtag((List<String>)map.get("hashtag"))
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

}
