package com.bod.bod.verification.entity;

import lombok.Getter;

@Getter
public enum Status {

  APPROVE(VerificationStatus.APPROVE),
  REJECT(VerificationStatus.REJECT),
  PENDING(VerificationStatus.PENDING);

  private final String status;

  Status(String status) {
	this.status = status;
  }

	public static class VerificationStatus {
	public static final String APPROVE = "APPROVE";
	public static final String REJECT = "REJECT";
	public static final String PENDING = "PENDING";
  }
}
