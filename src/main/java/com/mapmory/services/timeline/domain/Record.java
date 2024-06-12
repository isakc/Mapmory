package com.mapmory.services.timeline.domain;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.mapmory.services.community.domain.Reply;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Record {
	private int recordNo;
	private String recordUserId;
	private String recordTitle;
	private Double latitude;
	private Double longitude;
	private String checkpointAddress;
	private String checkpointDate;
	private String mediaName;
	private List<Map<String,Object>> imageTagList;
	private List<ImageTag> imageName;
	private List<ImageTag> hashtag;
	private int categoryNo;
	private String recordText;
	private int tempType;
	private String recordAddDate;
	private String sharedDate;
	private int updateCount;
	private Date d_DayDate;
	private int timecapsuleType;
	
	private Double distance;
	
//	public void setImageTagList(List<Map<String, Object>> imageTagList) {
//	    // 이미지 태그 리스트 초기화
//	    this.imageName = new ArrayList<>();
//	    this.hashtag = new ArrayList<>();
//	    
//	    for (Map<String, Object> map : imageTagList) {
//	        if ((int) map.get("imageTagType") == 1) {
//	            this.imageName.add(ImageTag.builder()
//	                    .imageTagOrder((int) map.get("imageTagOrder"))
//	                    .imageTagType((int) map.get("imageTagType"))
//	                    .imageTagText((String) map.get("imageTagText"))
//	                    .build());
//	        }
//	        if ((int) map.get("imageTagType") == 0) {
//	            this.hashtag.add(ImageTag.builder()
//	                    .imageTagOrder((int) map.get("imageTagOrder"))
//	                    .imageTagType((int) map.get("imageTagType"))
//	                    .imageTagText((String) map.get("imageTagText"))
//	                    .build());
//	        }
//	    }
//	}

	
//	public void setImageName(List<ImageTag> imageName) {
//		Map<String, Object> map;
//		for (ImageTag s : imageName) {
//			map=new HashMap<String, Object>();
//			map.put("imageTagType", 1);
//			map.put("imageTagText", s.getImageTagText());
//			this.imageTagList.add(map);
//		}
//	}
//	
//	public void setHashtag(List<ImageTag> hashtag) {
//		Map<String, Object> map;
//		for (ImageTag s : hashtag) {
//			map = new HashMap<String, Object>();
//			map.put("imageTagType", 0);
//			map.put("imageTagText", s.getImageTagText());
//			this.imageTagList.add(map);
//		}
//	}
	
//	public List<Map<String,Object>> getImageTagList(){
//	    List<Map<String,Object>> tempList = new ArrayList<Map<String,Object>>();
//	    
//	    // 이미지 태그가 있는 경우에만 처리
//	    if (this.imageName != null) {
//	        for (ImageTag imageTag : this.imageName) {
//	            Map<String,Object> map = new HashMap<String, Object>();
//	            map.put("imageTagType", 1);  // 이미지 태그의 타입을 1로 설정
//	            map.put("imageTagText", imageTag.getImageTagText());
//	            tempList.add(map);  // tempList에 이미지 태그 정보 추가
//	        }
//	    }
//	    
//	    // 해시태그가 있는 경우에만 처리
//	    if (this.hashtag != null) {
//	        for (ImageTag hashtag : this.hashtag) {
//	            Map<String,Object> map = new HashMap<String, Object>();
//	            map.put("imageTagType", 0);  // 해시태그의 타입을 0으로 설정
//	            map.put("imageTagText", hashtag.getImageTagText());
//	            tempList.add(map);  // tempList에 해시태그 정보 추가
//	        }
//	    }
//	    
//	    return tempList;
//	}

//	
//	public List<ImageTag> getImageName() {
//		this.imageName = new ArrayList<ImageTag>();
//		if (this.imageTagList != null) {
//			for (Map<String,Object> map : this.imageTagList) {
//				if ((int)map.get("imageTagType") == 1) {
//					this.imageName.add(ImageTag.builder()
//							.imageTagOrder((int)map.get("imageTagOrder"))
//							.imageTagType((int)map.get("imageTagType"))
//							.imageTagText((String)map.get("imageTagText"))
//							.build());
//				}
//			}
//		}
//		System.out.println("this.imageName : "+this.imageName);
//		return this.imageName;
//	}
//	
//	public List<ImageTag> getHashtag() {
//		this.hashtag=new ArrayList<ImageTag>();
//		if (this.imageTagList != null) {
//			for (Map<String,Object> map : this.imageTagList) {
//				if ((int)map.get("imageTagType") == 0) {
//					this.hashtag.add(ImageTag.builder()
//							.imageTagOrder((int)map.get("imageTagOrder"))
//							.imageTagType((int)map.get("imageTagType"))
//							.imageTagText((String)map.get("imageTagText"))
//							.build());
//				}
//			}
//		}
//		return this.hashtag;
//	}
}
