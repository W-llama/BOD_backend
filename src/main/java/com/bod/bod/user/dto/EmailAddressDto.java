package com.bod.bod.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmailAddressDto {

  	@Email(message = "유효한 이메일 주소를 입력해주세요.")
	@NotNull(message = "이메일 주소를 입력해주세요.")
	private String email;

	public EmailAddressDto(String email) {
	  this.email = email;
	}
}
