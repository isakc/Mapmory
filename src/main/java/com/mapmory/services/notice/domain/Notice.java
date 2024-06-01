package com.mapmory.services.notice.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.Data;

@Data
public class Notice {
	
    private int noticeNo;
    private String noticeTitle;
    private String noticeText;
    private String userId;
    private LocalDateTime noticeRegDate;
    private int noticeType;

    public String getnoticeRegDate() {
    	DateTimeFormatter notice = DateTimeFormatter.ofPattern("yyyy-mm-dd");
    	return notice.format(noticeRegDate);
    	
    }
}
