package com.mapmory.services.user.domain;

import java.time.LocalDate;

import com.mapmory.common.domain.Search;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString
@SuperBuilder
public class LoginSearch extends Search {

	private int year;
	private int month;
	private LocalDate selectLoginDate;
}