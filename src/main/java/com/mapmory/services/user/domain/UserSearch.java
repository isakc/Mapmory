package com.mapmory.services.user.domain;

import com.mapmory.common.domain.Search;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString
@SuperBuilder
public class UserSearch extends Search {

	// private int userId;
	private int role;
	private String userName;
	private String email;
}
