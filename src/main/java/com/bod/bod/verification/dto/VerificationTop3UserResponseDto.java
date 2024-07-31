package com.bod.bod.verification.dto;

import com.bod.bod.verification.entity.Verification;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VerificationTop3UserResponseDto {

  private Long verificationId;
  private String name;
  private String nickname;

  public VerificationTop3UserResponseDto(Verification verification) {
	this.verificationId = verification.getId();
	this.name = verification.getUser().getName();
	this.nickname = verification.getUser().getNickname();
  }
}
