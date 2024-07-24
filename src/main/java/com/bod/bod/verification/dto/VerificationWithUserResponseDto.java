package com.bod.bod.verification.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VerificationWithUserResponseDto {
  private Long verificationId;
  private String title;
  private String content;
  private String image;
  private String name;

  public VerificationWithUserResponseDto(Long verificationId, String title, String content, String image, String name) {
	this.verificationId = verificationId;
	this.title = title;
	this.content = content;
	this.image = image;
	this.name = name;
  }
}
