package com.mapmory.services.user.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.mapmory.common.util.ObjectStorageUtil;

@Component
public class UserServiceScheduler {
	
	@Autowired
	@Qualifier("objectStorageUtil")
	private ObjectStorageUtil objectStorageUtil;
	
	@Value("${object.tac.folderName}")
	private String folderName;
	
	// public final static String DOWNLOAD_FILE_PATH = "C:\\bitcampProject\\Mapmory\\src\\main\\resources\\static\\termsAndConditions";
	//  public final static String DOWNLOAD_FILE_PATH = "/usr/local/tomcat/webapps/ROOT/WEB-INF/classes/static/termsAndConditions";
	public final static String DOWNLOAD_FILE_PATH = "/src/main/resources/static/termsAndConditions";

	/**
	 * 매일 12시 정각에 object storage로부터 이용약관 file을 최신화.
	 * file upload는 지원하지 않음.
	 * @throws Exception
	 */
	@Scheduled(cron = "0 0 0 * * *")
	// protected
	public void fetchTermsAndConditions() throws Exception {
		
		objectStorageUtil.downloadFile(folderName, DOWNLOAD_FILE_PATH);
		
		
		
	}
	
	
	
	
	
}
