package com.mapmory.services.user.domain.auth.naver;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NaverProfile {

	private String email;
    private String nickname;
    private String profile_image;
    private String age;
    private String gender;
    private String id;
    private String name;
    private String birthday;
    private String birthyear;
    private String mobile;
    private String mobile_e164;
}