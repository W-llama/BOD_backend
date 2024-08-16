package com.bod.bod.user.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class EditIntroduceRequestDto {

	@Size(max = 255, message = "Introduce must be less than 255 characters")
	private String introduce;
}
