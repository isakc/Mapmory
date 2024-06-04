package com.mapmory.services.user.domain;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class Follow {

	private String userId;
	private String targetId;  // follow 할 때 사용
	// fb_type (0 : follow, 1 : block)
}
