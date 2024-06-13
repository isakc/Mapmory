package com.mapmory.services.user.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class Profile {

	User user;
	boolean isSubscribed;
	int totalFollowCount;
	int totalFollowerCount;
	int totalSharedListCount;
}
