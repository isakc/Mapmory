package com.mapmory.services.user.domain;

import java.time.LocalDateTime;
import java.util.List;

@Deprecated
public class LoginLog {

	private String userId;
	private List<LocalDateTime> loginDate;
}
