package com.mapmory.services.purchase.domain;

import lombok.Data;

@Data
public class IamportToken {
	private String access_token;
	private long now;
	private long expired_at;
}
