package com.bod.bod.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmailDto {

	private String email;
	private String title;
	private String text;

	public EmailDto(String email, String title, String text) {
	  this.email = email;
	  this.title = title;
	  this.text = text;
	}
}
