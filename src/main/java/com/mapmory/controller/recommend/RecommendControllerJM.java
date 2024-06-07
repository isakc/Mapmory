package com.mapmory.controller.recommend;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mapmory.common.util.ImageFileUtil;
import com.mapmory.common.util.ObjectStorageUtil;

@RestController
@RequestMapping("recommend/*")
public class RecommendControllerJM {

	@Autowired
	ObjectStorageUtil objectStorageUtil;
	
	@Value("${speech.folderName")
	String speechFolderName;
	
	@PostMapping("/upload")
	public ResponseEntity<Map<String, Object>> uploadAudio(@RequestParam("audioFile") MultipartFile audioFile) {
	    Map<String, Object> response = new HashMap<>();

	    try {
	        String originalFileName = audioFile.getOriginalFilename();
	        String uuidFileName = UUID.randomUUID().toString() + "_" + originalFileName;

	        // ObjectStorageUtil을 사용하여 S3에 업로드
	        objectStorageUtil.uploadFileToS3(audioFile, uuidFileName, speechFolderName);

	        response.put("success", true);
	        response.put("message", "파일 업로드 성공");
	        return ResponseEntity.ok(response);
	    } catch (Exception e) {
	        e.printStackTrace();
	        response.put("success", false);
	        response.put("message", "파일 업로드 중 오류 발생");
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
	}
}