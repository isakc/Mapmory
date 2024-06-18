package com.mapmory.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionData {

	private String userId;
	private int role;
	private int isKeepLogin;  // 0: 일반 로그인, 1: 로그인 유지 선택
}
