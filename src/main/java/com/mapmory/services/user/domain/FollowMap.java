package com.mapmory.services.user.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FollowMap {

	private String userId;        
    private String userName;     
    private String nickname;     
    private String profileImageName;
    private Byte hideProfile;  
}
