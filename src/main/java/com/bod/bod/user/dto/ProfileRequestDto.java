package com.bod.bod.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ProfileRequestDto {

	@NotBlank(message = "Nickname is mandatory")
	@Size(min = 3, max = 13, message = "Nickname must be between 3 and 13 characters")
	@Pattern(
		regexp = "^[a-zA-Z가-힣0-9]*$",
		message = "Nickname must contain only letters and numbers"
	)
	private String nickname;

	@Size(max = 255, message = "Introduce must be less than 255 characters")
	private String introduce;

	private String image;
}
