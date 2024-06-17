package com.mapmory.services.user.domain.auth.google;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GoogleAuthenticatorKey {

	private String encodedKey;
	private String userName;
	private String hostName;
}
