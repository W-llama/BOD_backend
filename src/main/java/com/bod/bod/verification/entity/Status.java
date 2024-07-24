package com.bod.bod.verification.entity;

public enum Status {

  APPROVE(VericationStatus.APPROVE),
  REJECT(VericationStatus.REJECT),
  PENDING(VericationStatus.PENDING);

  private final String status;

  Status(String status) {
	this.status = status;
  }

  public String getStatus() {
	return this.status;
  }

  public static class VericationStatus {
	public static final String APPROVE = "APPROVE";
	public static final String REJECT = "REJECT";
	public static final String PENDING = "PENDING";
  }
}
