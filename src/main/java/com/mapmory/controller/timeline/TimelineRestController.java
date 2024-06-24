package com.mapmory.controller.timeline;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mapmory.common.domain.Search;
import com.mapmory.common.util.ClovaSpeechClient;
import com.mapmory.common.util.ClovaSpeechClient.NestRequestEntity;
import com.mapmory.common.util.ContentFilterUtil;
import com.mapmory.services.timeline.domain.Category;
import com.mapmory.services.timeline.domain.Record;
import com.mapmory.services.timeline.service.TimelineService;
import com.mapmory.common.util.ObjectStorageUtil;
import com.mapmory.common.util.TimelineUtil;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;

@RestController
@RequestMapping("timeline/rest/*")
public class TimelineRestController {
	@Autowired
	@Qualifier("timelineService")
	private TimelineService timelineService;
	
	@Autowired
	private TimelineUtil timelineUtil;
	
	@Autowired
	private ObjectStorageUtil objectStorageUtil;
	
	@Value("${speech.folderName}")
	private String speechFolderName;

	@Value("${page.Size}")
	private int pageSize;
	
	@Value("${page.Unit}")
	private int pageUnit;
	
	public TimelineRestController(){
		System.out.println("TimelineRestController default Contrctor call : " + this.getClass());
	}
	
	@PostMapping("addTimeline")
	public ResponseEntity<Map<String,Object>> addTimelineView(
			@RequestBody Record record,
			Map<String,Object> map) throws Exception,IOException {
		map=new HashMap<String, Object>();
		record.setRecordTitle(record.getCheckpointAddress()+"_"+LocalDateTime.now().toString().replace("T"," ").split("\\.")[0]);
		record.setUpdateCount(-1);
		record.setTempType(0);
		record.setTimecapsuleType(0);
		record.setCategoryNo(0);
		String text="";
		int recordNo=timelineService.addTimeline(record);
		if(recordNo!=0) {
			text+="체크포인트가 저장 완료 WHYNOT!"+timelineService.getDetailTimeline(recordNo);
		}else {
			text+="체크포인트 저장 안됨";
		}
		map.put("text", text);
		return ResponseEntity.ok(map);
	}
	
	@GetMapping("deleteImage")
	public ResponseEntity<Map<String,Object>> deleteImage(
			@RequestParam(name = "imageNo", required = true) int imageNo,
			@RequestParam(name = "imageName", required = true) String imageName,
			Map<String,Object> map) throws Exception,IOException {
		map=new HashMap<String, Object>();
		String text="";
		int deleteSuccess=timelineService.deleteImage(imageNo);
		if(deleteSuccess!=0) {
			timelineUtil.deleteImageFile(imageName);
			text+="사진 삭제 완료.";
		}else {
			text = "사진 삭제 실패.";
		}
		map.put("text", text);
		return ResponseEntity.ok(map);
	}
	
	@GetMapping("deleteMedia")
	public ResponseEntity<Map<String, Object>> deleteMedia(
			@RequestParam(name = "recordNo", required = true) int recordNo,
			@RequestParam(name = "mediaName", required = true) String mediaName,
			Map<String, Object> map) throws Exception, IOException {
		map = new HashMap<String, Object>();
		String text="";
		int deleteSuccess=timelineService.deleteMedia(recordNo);
		if(deleteSuccess!=0) {
			timelineUtil.deleteMediaFile(mediaName);
			text = "영상 삭제 완료.";
		} else {
			text = "영상 삭제 실패.";
		}
		map.put("text", text);
		return ResponseEntity.ok(map);
	}
	
	@PostMapping("addCategory")
	public ResponseEntity<Map<String, Object>> addCategory(@ModelAttribute Category category,
			@RequestParam(name="categoryImojiFile",required = true) MultipartFile categoryImojiFile,
			Map<String, Object> map
			) throws Exception,IOException {
		map = new HashMap<String, Object>();
		category = timelineUtil.uploadImojiFile(category, categoryImojiFile) ;
		boolean success=timelineService.addCategory(category)!=0 ? true:false;
		map.put("success", success);
		return ResponseEntity.ok(map);
	}
	
	@PostMapping("updateCategory")
	public ResponseEntity<Map<String, Object>> updateCategory(@ModelAttribute Category category,
			@RequestParam(name="categoryImojiFile",required = false) MultipartFile categoryImojiFile,
			Map<String, Object> map
			) throws Exception,IOException {
		//System.out.println("category : "+category);
		map = new HashMap<String, Object>();
		category.setCategoryImoji( timelineUtil.imojiUrlToName(category.getCategoryImoji()) );
		category = timelineUtil.uploadImojiFile(category, categoryImojiFile) ;
		boolean success=timelineService.updateCategory(category)!=0 ? true:false;
		map.put("success", success);
		return ResponseEntity.ok(map);
	}
	
	@GetMapping("deleteCategory")
	public ResponseEntity<Map<String, Object>> deleteCategory(
			@RequestParam(name="categoryNo",required = true) int categoryNo,
			Map<String, Object> map
			) throws Exception,IOException {
		map = new HashMap<String, Object>();
		boolean success=timelineService.deleteCategory(categoryNo)!=0 ? true:false;
		map.put("success", success);
		return ResponseEntity.ok(map);
	}
	
	//이미지 가져오기 용/서버로드보다 응답이 빠르면 파일이 깨진다. 지원용
	@GetMapping("/{type}/{name}")
	public byte[] getImage(@PathVariable String type, @PathVariable String name) throws Exception {
		return timelineUtil.getFile(type, name);
	}
	
	@GetMapping("checkBadWord")
	public ResponseEntity<Map<String, Object>> checkBadWord(
			@RequestParam(name = "text", required = true) String text,
			Map<String, Object> map) throws Exception, IOException {
		map = new HashMap<String, Object>();
		map.put("badWord", ContentFilterUtil.checkBadWord(text) );
		return ResponseEntity.ok(map);
	}
	
	@PostMapping("upload")
	public ResponseEntity<Map<String, Object>> uploadAudio(@RequestParam("audioFile") MultipartFile audioFile) throws Exception {
		// 파일 저장 위치
        String uploadDir = "C:/workPJT/workspace/Mapmory/src/main/resources/static/temp/";
        File uploadDirFile = new File(uploadDir);
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();  // 디렉토리가 없으면 생성
        }
		File uploadFile = new File(uploadDir, audioFile.getOriginalFilename());
		audioFile.transferTo(uploadFile);
		
        // 추가 처리 로직 (예: 데이터베이스 저장 등)
        System.out.println("파일 저장 위치: " + uploadFile.getAbsolutePath());
        
		final ClovaSpeechClient clovaSpeechClient = new ClovaSpeechClient();
		NestRequestEntity requestEntity = new NestRequestEntity();
		final String result =
			clovaSpeechClient.upload(uploadFile, requestEntity);
		//final String result = clovaSpeechClient.url("file URL", requestEntity);
		//final String result = clovaSpeechClient.objectStorage("Object Storage key", requestEntity);
		System.out.println(result);
		
//	    Map<String, Object> response = new HashMap<>();
//
//	    try {
//	        String originalFileName = audioFile.getOriginalFilename();
//	        String uuidFileName = UUID.randomUUID().toString() + "_" + originalFileName;
//
//	        // ObjectStorageUtil을 사용하여 S3에 업로드
//	        objectStorageUtil.uploadFileToS3(audioFile, uuidFileName, speechFolderName);
//
//	        response.put("success", true);
//	        response.put("message", "파일 업로드 성공");
//	        
//	        return ResponseEntity.ok(response);
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        response.put("success", false);
//	        response.put("message", "파일 업로드 중 오류 발생");
//	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//	    }
		return null;
	}
	
	// 대민 지원
	// 개수도 주세요
	/**
	 * 
	 * @param map
	 * @param userId
	 * @param currentPage
	 * @param logsType  :: 0: 공유한 기록, 1: 즐겨찾기, 2: 좋아요
	 * @return
	 * @throws Exception
	 * @throws IOException
	 */
	@GetMapping("getProfileTimelineList")
	public ResponseEntity<Map<String, Object>> getProfileTimelineList(Map<String, Object> map,
			@RequestParam(name = "userId", required = true) String userId,
			@RequestParam(name = "currentPage", required = true) int currentPage,
			@RequestParam(name = "logsType", required = true) Integer logsType) throws Exception, IOException {
		map = new HashMap<String, Object>();
		
		System.out.println("currentPage = " + currentPage);
		
		Search search = Search.builder().userId(userId).limit(pageSize).currentPage(currentPage).logsType(logsType).build();
		map.put("timelineList", timelineService.getProfileTimelineList(search));
		map.put("count", timelineService.getProfileTimelineCount(search));
		
		// System.out.println(map);
		
		return ResponseEntity.ok(map);
	}

}
