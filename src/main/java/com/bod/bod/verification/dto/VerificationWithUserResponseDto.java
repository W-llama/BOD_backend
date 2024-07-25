package com.bod.bod.verification.dto;

import com.bod.bod.verification.entity.Verification;
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

  public VerificationWithUserResponseDto(Verification verification) {
	this.verificationId = verification.getId();
	this.title = verification.getTitle();
	this.content = verification.getContent();
	this.image = verification.getImageUrl();
	this.name = verification.getUser().getName();
  }
}
