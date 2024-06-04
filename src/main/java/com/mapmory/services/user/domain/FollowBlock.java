package com.mapmory.services.user.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class FollowBlock {

	private String userId;
	private String targetId;  // follow 할 때 사용
	private int fb_type; // (0 : follow, 1 : block)
}
