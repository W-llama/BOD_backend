package com.bod.bod.user.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class EditNickNameRequestDto {

	@Pattern(
		regexp = "^[a-zA-Z가-힣0-9]*$",
		message = "Nickname must contain only letters and numbers"
	)
	private String nickname;
}
