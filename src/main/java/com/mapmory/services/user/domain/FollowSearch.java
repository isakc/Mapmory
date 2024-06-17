package com.mapmory.services.user.domain;

import com.mapmory.common.domain.Search;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@ToString
public class FollowSearch extends Search {

	private String myUserId;
	private int selectFollow;  // 0 :: follow 검색, 1 :: follower 검색
}
