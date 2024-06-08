package com.mapmory.controller.timeline;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mapmory.common.util.ObjectStorageUtil;

import java.io.File;
import java.nio.charset.StandardCharsets;
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
	ObjectStorageUtil objectStorageUtil;
	
	@Value("${speech.folderName}")
	String speechFolderName;
	
	@PostMapping("/upload")
	public ResponseEntity<Map<String, Object>> uploadAudio(@RequestParam("audioFile") MultipartFile audioFile) {
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
//	        return ResponseEntity.ok(response);
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        response.put("success", false);
//	        response.put("message", "파일 업로드 중 오류 발생");
//	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//	    }
		return null;
	}

}
