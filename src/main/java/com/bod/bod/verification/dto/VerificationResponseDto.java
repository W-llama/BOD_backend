package com.bod.bod.verification.dto;

import com.bod.bod.verification.entity.Verification;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VerificationResponseDto {
	private String title;
	private String content;
	private String image;

	public VerificationResponseDto(String title, String content, String image) {
		this.title = title;
		this.content = content;
		this.image = image;
	}
}
