package com.bod.bod.user.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmailAuthenticationDto {

  @NotNull(message = "이메일 주소를 입력해주세요.")
  private String email;

  @NotNull(message = "인증번호를 입력해주세요.")
  @Size(min = 6, max = 6, message = "인증번호 6자리를 입력해주세요.")
  private String code;

  public EmailAuthenticationDto(String email, String code) {
	this.email = email;
	this.code = code;
  }
}
