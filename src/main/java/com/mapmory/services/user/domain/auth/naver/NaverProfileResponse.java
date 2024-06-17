package com.mapmory.services.user.domain.auth.naver;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NaverProfileResponse {

	private String resultcode;
	private String message;
	private NaverProfile response;
}
