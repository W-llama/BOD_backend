package com.bod.bod.verification.dto;

import com.bod.bod.verification.entity.Status;
import com.bod.bod.verification.entity.Verification;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VerificationWithChallengeResponseDto {

  private Long verificationId;
  private String challengeTitle;
  private Status status;
  private LocalDateTime createdAt;

  public VerificationWithChallengeResponseDto(Long verificationId, String challengeTitle, Status status, LocalDateTime createdAt) {
	this.verificationId = verificationId;
	this.challengeTitle = challengeTitle;
	this.status = status;
	this.createdAt = createdAt;
  }

}
