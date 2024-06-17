package com.mapmory.services.user.domain.auth.google;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GoogleToken {
	
	private String access_token; 
    private String expires_in;   
    private String refresh_token;   
    private String scope;
    private String token_type;   
    private String id_token; 
}