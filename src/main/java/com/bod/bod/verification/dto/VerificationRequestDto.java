package com.bod.bod.verification.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class VerificationRequestDto {

  @NotBlank(message = "인증 제목을 입력해주세요.")
  private String title;
  @NotBlank(message = "인증 내용을 입력해주세요.")
  private String content;

}
