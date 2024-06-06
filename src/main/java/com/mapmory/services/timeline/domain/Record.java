package com.mapmory.services.timeline.domain;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.print.DocFlavor.STRING;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Record {
	private int recordNo;
	private String recordUserId;
	private String recordTitle;
	private Double latitude;
	private Double longitude;
	private String checkpointAddress;
	private LocalDateTime checkpointDate;
	private String mediaName;
	private List<Map<String,Object>> imageTag;
	private List<String> imageName;
	private List<String> hashtag;
	private int categoryNo;
	private String recordText;
	private int tempType;
	private LocalDateTime recordAddDate;
	private LocalDateTime sharedDate;
	private int updateCount;
	private Date d_DayDate;
	private int timecapsuleType;
	
	private Double distance;
	
	public List<String> getImageName() {
		this.imageName=new ArrayList<String>();
		for (Map<String, Object> map : this.imageTag) {
				if ((int) map.get("imageTagType") == 1) {
					this.imageName.add((String) map.get("imageTagText"));
				}
			}
		return this.imageName;
	}
	
	public List<String> getHashtag() {
		this.hashtag=new ArrayList<String>();
		for (Map<String, Object> map : this.imageTag) {
				if ((int) map.get("imageTagType") == 0) {
					this.hashtag.add((String) map.get("imageTagText"));
				}
			}
		return this.hashtag;
	}
}
