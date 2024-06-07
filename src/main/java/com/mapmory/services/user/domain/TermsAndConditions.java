package com.mapmory.services.user.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TermsAndConditions {

	private String title;
	private boolean required;
	private StringBuilder contents;
	
	public TermsAndConditions() {
		
	}
	
	public TermsAndConditions(String title, boolean required, StringBuilder contents) {
		this.title = title;
		this.required = required;
		this.contents = contents;
	}
	
	public boolean getRequired() {
		return required;
	}
	
	public void setRequired(boolean required) {
		this.required = required;
	}
	
}
