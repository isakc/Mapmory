package com.mapmory.services.user.domain.auth.naver;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NaverAuthToken {

	private String access_token;
    private String refresh_token;
    private String token_type;
    private String expires_in;

    public NaverAuthToken() {
    }
}
