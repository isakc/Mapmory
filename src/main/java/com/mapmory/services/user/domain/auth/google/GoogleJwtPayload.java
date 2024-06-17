package com.mapmory.services.user.domain.auth.google;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GoogleJwtPayload {

	private String iss;
    private String azp;
    private String aud;
    private String sub;
    private String email;
    private boolean email_verified;
    private String at_hash;
    private String name;
    private String picture;
    private String given_name;
    private String family_name;
    private long iat;
    private long exp;
}