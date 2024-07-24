package com.bod.bod.verification.dto;

import com.bod.bod.verification.entity.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VerificationResponseDto {
  	private Long verificationId;
	private String title;
	private String content;
	private String image;
	private Status status;

	public VerificationResponseDto(Long verificationId, String title, String content, String image, Status status) {
		this.verificationId = verificationId;
	  	this.title = title;
		this.content = content;
		this.image = image;
		this.status = status;
	}
}
