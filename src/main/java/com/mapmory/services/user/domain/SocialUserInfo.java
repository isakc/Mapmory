package com.mapmory.services.user.domain;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SocialUserInfo {

    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private String gender;
    private Date birthday;
    
}
