package com.mapmory.controller.recommend;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
	public void uploadAudio(@RequestParam("audioFile") MultipartFile audioFile) throws Exception {
	    String originalFileName = audioFile.getOriginalFilename();
	    String uuidFileName = ImageFileUtil.getProductImageUUIDFileName(originalFileName); // UUID 파일명 생성
	    
	    // ObjectStorageUtil을 사용하여 S3에 업로드
	    objectStorageUtil.uploadFileToS3(audioFile, uuidFileName, speechFolderName);
	}
}