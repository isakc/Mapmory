package com.mapmory.services.user.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class FollowBlock {

	private String userId;
	private String targetId;  // follow 할 때 사용
	private int fb_type; // (0 : follow, 1 : block)
}
