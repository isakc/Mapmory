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
    private int isSubscribed;  // 0 : 구독 안함, 1 : 구독중
    private int isFollow;  // 0 : 팔로우 안함, 1 : 팔로우중
}
