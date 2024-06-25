package com.mapmory.common.util;

import java.io.FileNotFoundException;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.mapmory.services.timeline.domain.Category;
import com.mapmory.services.timeline.domain.ImageTag;
import com.mapmory.services.timeline.domain.MapRecord;
import com.mapmory.services.timeline.domain.Record;
import com.mapmory.services.timeline.domain.SharedRecord;
import com.mapmory.services.timeline.dto.SummaryRecordDto;

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
	
	@Autowired
	private ContentFilterUtil contentFilterUtil;
	
    @Autowired
    private ObjectStorageUtil objectStorageUtil;
	
	@Value("${object.timeline.image}")
	private String imageFileFolder;
	
	@Value("${object.timeline.imoji}")
	private String imojiFileFolder;
	
	@Value("${object.timeline.media}")
	private String mediaFileFolder;
    
    @Value("${cdn.url}")
    private String cdnUrl;
	
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	//map을 record로 묶어주는 기능
		public static Record mapToRecord(Map<String, Object> map) {
			List<Map<String,Object>> imageTagList =(List<Map<String,Object>>)map.get("imageTagList");
			System.out.println("(Date)map.get(\"d_DayDate\")==null ? null:((Date)map.get(\"d_DayDate\")).toString()"+((Date)map.get("d_DayDate")==null ? null:((Date)map.get("d_DayDate")).toString()));
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
					.categoryName((String)map.get("categoryName"))
					.categoryImoji((String)map.get("categoryImoji"))
					.recordText(map.get("recordText") ==null ? "" : (String)map.get("recordText"))
					.tempType((Integer)map.get("tempType"))
					.recordAddDate((String)map.get("recordAddDate"))
					.sharedDate((String)map.get("sharedDate"))
					.updateCount((Integer)map.get("updateCount"))
					.d_DayDate((Date)map.get("d_DayDate")==null ? null:((Date)map.get("d_DayDate")).toString())
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
					.recordText((String)map.get("recordText"))
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
					.timecapsuleType((Integer)map.get("timecapsuleType"))
					.nickname((String)map.get("nickname"))
					.profileImageName((String)map.get("profileImageName"))
					.subscriptionEndDate((long)map.get("subscriptionEndDate"))
					.categoryNo((int)map.get("categoryNo"))
					.categoryName((String)map.get("categoryName"))
					.categoryImoji((String)map.get("categoryImoji"))
					.bookmark((long)map.get("bookmark"))
					.likeCount((long)map.get("likeCount"))
					.dislikeCount((long)map.get("dislikeCount"))
					.replyCount((long)map.get("replyCount"))
					.logsCount((long)map.get("logsCount"))
					.build();
			return sharedRecord;
		}
		
		public static MapRecord mapToMapRecord(Map<String, Object> map) {
//			List<Map<String,Object>> imageTagList =(List<Map<String,Object>>)map.get("imageTagList");
			
			MapRecord mapRecord=MapRecord.builder()
					.recordNo((int)map.get("recordNo"))
					.recordUserId((String)map.get("recordUserId"))
					.nickName((String)map.get("nickName"))
					.profileImageName((String)map.get("profileImageName"))
					.recordTitle((String)map.get("recordTitle"))
					.latitude((Double)map.get("latitude"))
					.longitude((Double)map.get("longitude"))
					.checkpointAddress((String)map.get("checkpointAddress"))
					.checkpointDate((String)map.get("checkpointDate"))
					.mediaName(map.get("mediaName") ==null ? "" : (String)map.get("mediaName"))
//					.imageName(listToImage(imageTagList))
//					.hashtag(listToHashtag(imageTagList))
					.imageName((List<ImageTag>) map.get("imageName"))
					.hashtag((List<ImageTag>) map.get("hashtag"))
					.categoryNo((Integer)map.get("categoryNo"))
					.categoryName((String)map.get("categoryName"))
					.categoryImoji((String)map.get("categoryImoji"))
					.recordText(map.get("recordText") ==null ? "" : (String)map.get("recordText"))
					.tempType((Integer)map.get("tempType"))
					.recordAddDate((String)map.get("recordAddDate"))
					.sharedDate((String)map.get("sharedDate"))
					.updateCount((Integer)map.get("updateCount"))
					.timecapsuleType((Integer)map.get("timecapsuleType"))
					.build();
			return mapRecord;
		}
		
		public static Category mapToCategory(Map<String, Object> map) {
			
			Category category=Category.builder()
					.categoryNo((int)map.get("categoryNo"))
					.categoryName((String)map.get("categoryName"))
					.categoryImoji((String)map.get("categoryImoji"))
					.build();
			return category;
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
		
		public static String hashtagListToText(List<ImageTag> hashtag){
			String hashtagText="";
			for(ImageTag imageTag:hashtag) {
				hashtagText+=imageTag.getImageTagText()+" ";
			}
			return hashtagText.trim();
		}
		
		public static List<ImageTag> hashtagTextToList(String hashtagText,int recordNo){
			String[] hashtagArr=hashtagText.replace(" ", "").split("#");
			List<ImageTag> hashtagList=new ArrayList<ImageTag>();
			for(int i=1;i<hashtagArr.length;i++) {
				if(i<=20) {
					if(hashtagArr[i].length()<=20) {
					hashtagList.add(ImageTag
						.builder()
						.imageTagText("#"+hashtagArr[i])
						.imageTagType(0)
						.recordNo(recordNo)
						.build());
					}
				}
			}
			return hashtagList;
		}
		
		public static Record validateRecord(Record record) {
			if(record.getRecordTitle()==null||record.getRecordTitle().trim().equals(""))
				record.setRecordTitle(record.getCheckpointAddress()+"_"+record.getCheckpointDate());
			
			if(record.getRecordAddDate()!=null&&!record.getRecordAddDate().trim().equals(""))
				record.setRecordAddDate(parseDateTime(record.getRecordAddDate()).toString());
			else record.setRecordAddDate(null);
			
			if(record.getSharedDate()!=null&&!record.getSharedDate().trim().equals(""))
				record.setSharedDate(parseDateTime(record.getSharedDate()).toString());
			else record.setSharedDate(null);
			
			if(record.getD_DayDate()!=null&&!record.getD_DayDate().trim().equals("")) {
				System.out.println("record.setD_DayDate(parseDate(record.getD_DayDate()).toString()); _"+ record.getD_DayDate()+"_");
				record.setD_DayDate(parseDate(record.getD_DayDate()).toString());
			}
			else record.setD_DayDate(null);
			
			return record;
		}
		
		// 문자열이 날짜 형식인지 확인하고 java.sql.Date로 변환
		public static LocalDate parseDate(String dateStr) {
	        try {
	            LocalDate localDate = LocalDate.parse(dateStr, DATE_FORMATTER);
	            return localDate;
	        } catch (DateTimeParseException e) {
	            System.out.println("Invalid date format: " + dateStr);
	            return null;
	        }
	    }

	    // 문자열이 일시 형식인지 확인하고 java.sql.Timestamp로 변환
	    public static LocalDateTime parseDateTime(String dateTimeStr) {
	        try {
	            LocalDateTime localDateTime = LocalDateTime.parse(dateTimeStr, DATETIME_FORMATTER);
	            return localDateTime;
	        } catch (DateTimeParseException e) {
	            System.out.println("Invalid date time format: " + dateTimeStr);
	            return null;
	        }
	    }
	    
	    public static String updateCountToText(int updateCount) {
			String text="";
			if(updateCount==-1) text+="미기록";
			if(updateCount==0) text+="기록됨";
			if(updateCount>0) text+=updateCount+"번째 수정됨";
	    	return text;
	    }
	    
	    //rest로 파일을 부르면 오류남
	    public byte[] getFile(String type,String name) throws Exception {
	    	byte[] bytes;
			switch (type) {

			case "imoji":
				bytes = objectStorageUtil.getImageBytes(name, imojiFileFolder);
				break;
			case "image":
				bytes = objectStorageUtil.getImageBytes(name, imageFileFolder);
				break;
			case "media":
				bytes = objectStorageUtil.getImageBytes(name, mediaFileFolder);
				break;
			default:
				bytes = null;
			}

			return bytes;
	    }
		
		public Record imageNameToUrl(Record record) {
			if (!(record.getImageName() == null)) {
				List<ImageTag> imageName = new ArrayList<ImageTag>();
				for (ImageTag image : record.getImageName()) {
					image.setImageTagText(objectStorageUtil.getImageUrl(image.getImageTagText(), imageFileFolder));
					imageName.add(image);
				}
				record.setImageName(imageName);
			}
			return record;
		}

		public Record imojiNameToUrl(Record record) {
			if (!(record.getCategoryImoji() == null || record.getCategoryImoji().equals(""))) {
				record.setCategoryImoji(objectStorageUtil.getImageUrl(record.getCategoryImoji(), imojiFileFolder));
			}
			return record;
		}

		public Record mediaNameToUrl(Record record) {
			if (!(record.getMediaName() == null || record.getMediaName().equals(""))) {
				record.setMediaName(objectStorageUtil.getImageUrl(record.getMediaName(), mediaFileFolder));
			}
			return record;
		}
		
		public List<Category> categoryImojiListToUrl(List<Category> categoryList) {
				List<Category> tempList = new ArrayList<Category>();
				for (Category category : categoryList) {
					if(!(category.getCategoryImoji()==null ||category.getCategoryImoji().isEmpty())) {
						category.setCategoryImoji(objectStorageUtil.getImageUrl(category.getCategoryImoji(), imojiFileFolder));
					}
					tempList.add(category);
				}
			return tempList;
		}
		
		public Category categoryImojiNameToUrl(Category category) {
					if(!(category.getCategoryImoji()==null ||category.getCategoryImoji().isEmpty())) {
						category.setCategoryImoji(objectStorageUtil.getImageUrl(category.getCategoryImoji(), imojiFileFolder));
					}
			return category;
		}
		
		public Record imageNameToByte(Record record) throws Exception {
			if (!(record.getImageName() == null)) {
				List<ImageTag> imageName = new ArrayList<ImageTag>();
				for (ImageTag image : record.getImageName()) {
					try {
					image.setImageTagByte(Base64.getEncoder().encodeToString(
									objectStorageUtil.getImageBytes(image.getImageTagText(), imageFileFolder)));
					}catch(FileNotFoundException e){
						image.setImageTagByte(null);
						System.out.println("데이터베이스에 파일이 존재 하나 ObjectStorige에 없습니다.:"+e);
					}
					imageName.add(image);
				}
				record.setImageName(imageName);
			}
			return record;
		}

		public Record imojiNameToByte(Record record) throws Exception {
			if (!(record.getCategoryImoji() == null || record.getCategoryImoji().equals(""))) {
				try {
				record.setCategoryImojiByte(Base64.getEncoder().encodeToString(
								objectStorageUtil.getImageBytes(
										record.getCategoryImoji(), imojiFileFolder)));
				}catch(FileNotFoundException e){
					record.setCategoryImojiByte(null);
					System.out.println("데이터베이스에 파일이 존재 하나 ObjectStorige에 없습니다.:"+e);
				}
			}
			return record;
		}

		public Record mediaNameToByte(Record record) throws Exception {
			if (!(record.getMediaName() == null || record.getMediaName().equals(""))) {
				try {
				record.setMediaByte(Base64.getEncoder().encodeToString(
						objectStorageUtil.getImageBytes(record.getMediaName(), mediaFileFolder)));
				}catch(FileNotFoundException e){
					record.setMediaByte(null);
					System.out.println("데이터베이스에 파일이 존재 하나 ObjectStorige에 없습니다.:"+e);
				}
			}
			return record;
		}

		public List<SummaryRecordDto> summaryFileNameToByte(List<SummaryRecordDto> recordList) throws Exception {
			List<SummaryRecordDto> tempList=new ArrayList<SummaryRecordDto>();
			for(SummaryRecordDto record : recordList) {
				if (!(record.getMediaName() == null || record.getMediaName().equals(""))) {
					try {
					record.setMediaByte(Base64.getEncoder().encodeToString(
							objectStorageUtil.getImageBytes(record.getMediaName(), mediaFileFolder)));
					tempList.add(record);
					}catch(FileNotFoundException e){
						record.setMediaByte(null);
						System.out.println("데이터베이스에 파일이 존재 하나 ObjectStorige에 없습니다.:"+e);
					}
				}
				if (!(record.getImageName() == null || record.getImageName().equals(""))) {
					try {
					record.setImageByte(Base64.getEncoder().encodeToString(
							objectStorageUtil.getImageBytes(record.getImageName(), imageFileFolder)));
					tempList.add(record);
					}catch(FileNotFoundException e){
						record.setMediaByte(null);
						System.out.println("데이터베이스에 파일이 존재 하나 ObjectStorige에 없습니다.:"+e);
					}
				}
			}
			return tempList;
		}
		
		public List<Category> categoryImojiListToByte(List<Category> categoryList) throws Exception {
				List<Category> tempList = new ArrayList<Category>();
				for (Category category : categoryList) {
					if(!(category.getCategoryImoji()==null ||category.getCategoryImoji().isEmpty())) {
						category.setCategoryImojiByte(Base64.getEncoder().encodeToString(
								objectStorageUtil.getImageBytes(category.getCategoryImoji(), imojiFileFolder)));
					}
					tempList.add(category);
				}
			return tempList;
		}
		
		public Category categoryImojiNameToByte(Category category) throws Exception {
					if(!(category.getCategoryImoji()==null ||category.getCategoryImoji().isEmpty())) {
						category.setCategoryImojiByte(Base64.getEncoder().encodeToString(
								objectStorageUtil.getImageBytes(category.getCategoryImoji(), imojiFileFolder)));
					}
			return category;
		}

		public String imageUrlToName(String imageUrl) {
			if (!(imageUrl == null || imageUrl.equals(""))) {
				imageUrl = imageUrl.replace(cdnUrl+imageFileFolder+"/","");
			}
			return imageUrl;
		}

		public String mediaUrlToName(String mediaUrl) {
			if (!(mediaUrl == null || mediaUrl.equals(""))) {
				mediaUrl = mediaUrl.replace(cdnUrl+mediaFileFolder+"/","");
			}
			return mediaUrl;
		}
		public String imojiUrlToName(String imojiUrl) {
			if (!(imojiUrl == null || imojiUrl.equals(""))) {
				imojiUrl = imojiUrl.replace(cdnUrl+imojiFileFolder+"/","");
			}
			return imojiUrl;
		}
		
		public Record uploadImageFile(Record record,List<MultipartFile> imageFile) throws Exception {
			List<ImageTag> imageName=new ArrayList<ImageTag>();
			if( imageFile!=null && !imageFile.isEmpty() ) {
				
			System.out.println("List<MultipartFile> imageFile : "+imageFile);
			
			for (MultipartFile image : imageFile) {
				if (image==null
						|| image.isEmpty() 
						|| image.getOriginalFilename() == null 
						|| image.getOriginalFilename().isEmpty()) {
	            System.out.println("Invalid file name: " + image.getOriginalFilename());
	            continue;
				}
				if (contentFilterUtil.checkBadImage(image) == false) {
					System.out.println("이미지 검사 통과");
					String uuid = ImageFileUtil.getImageUUIDFileName(image.getOriginalFilename());
					objectStorageUtil.uploadFileToS3(image, uuid, imageFileFolder);
					imageName.add(
							ImageTag.builder().recordNo(record.getRecordNo()).imageTagType(1).imageTagText(uuid).build());
				} else {
					System.out.println(image.getOriginalFilename() + " 유해 이미지, 차단");
				}
			}
		}
			record.setImageName(imageName);
			
			return record;
			
		}
		
		public Record uploadMediaFile(Record record,MultipartFile mediaFile) throws Exception {

			if (mediaFile==null
					|| mediaFile.isEmpty() 
					|| mediaFile.getOriginalFilename() == null
					|| mediaFile.getOriginalFilename().isEmpty()) {
				System.out.println("Invalid file name: " + record.getMediaName());
				return record;
			}
			String uuid = ImageFileUtil.getImageUUIDFileName(mediaFile.getOriginalFilename());
			objectStorageUtil.uploadFileToS3(mediaFile, uuid, mediaFileFolder);
			record.setMediaName(uuid);

			return record;
			
		}
		
		public Category uploadImojiFile(Category category,MultipartFile imojiFile) throws Exception {

			if (imojiFile==null
					||imojiFile.isEmpty() 
					|| imojiFile.getOriginalFilename() == null
					|| imojiFile.getOriginalFilename().isEmpty()) {
				System.out.println("Invalid file name: " + category.getCategoryImoji());
				return category;
			}
			String uuid = ImageFileUtil.getImageUUIDFileName(imojiFile.getOriginalFilename());
			objectStorageUtil.uploadFileToS3(imojiFile, uuid, imojiFileFolder);
			category.setCategoryImoji(uuid);

			return category;
			
		}
		
		public void deleteImageFile(String imageName) throws Exception {
//			imageName = imageUrlToName(imageName);
			objectStorageUtil.deleteFile(imageName, imageFileFolder);
			System.out.println("deleteImageFile : "+ imageName);
		}
		
		public void deleteMediaFile(String mediaName) throws Exception {
//			mediaName = mediaUrlToName(mediaName);
			objectStorageUtil.deleteFile(mediaName, mediaFileFolder);
			System.out.println("deleteImageFile : "+ mediaName);
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
