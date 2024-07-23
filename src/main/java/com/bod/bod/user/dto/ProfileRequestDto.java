package com.bod.bod.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ProfileRequestDto {

	@NotBlank(message = "Nickname is mandatory")
	@Size(min = 2, max = 50, message = "Nickname must be between 2 and 50 characters")
	private String nickname;

	@Size(max = 255, message = "Introduce must be less than 255 characters")
	private String introduce;
}
