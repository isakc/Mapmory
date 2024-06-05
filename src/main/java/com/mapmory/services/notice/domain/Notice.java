package com.mapmory.services.notice.domain;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.Data;

@Data
public class Notice {
	
    private int noticeNo;
    private String noticeTitle;
    private String noticeText;
    private String userId;
    private Date noticeRegDate;
    private Integer noticeType;

}
