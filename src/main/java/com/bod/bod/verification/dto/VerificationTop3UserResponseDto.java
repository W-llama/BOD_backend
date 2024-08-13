package com.bod.bod.verification.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VerificationTop3UserResponseDto {

  private Long verificationId;
  private String name;
  private String nickname;

  public VerificationTop3UserResponseDto(Long verificationId, String name, String nickname) {
	this.verificationId = verificationId;
	this.name = name;
	this.nickname = nickname;
  }
}
