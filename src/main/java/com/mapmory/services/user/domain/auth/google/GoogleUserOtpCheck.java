package com.mapmory.services.user.domain.auth.google;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GoogleUserOtpCheck {

	private String userCode;
	private String encodedKey;
}
