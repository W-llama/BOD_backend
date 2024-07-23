package com.bod.bod.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class SignUpRequestDto {

	@NotBlank(message = "Username is mandatory")
	@Size(min = 3, max = 13, message = "Username must be between 3 and 13 characters")
	private String username;

	@NotBlank(message = "Email is mandatory")
	@Email(message = "Email should be valid")
	@Size(max = 20, message = "Email must be less than 20 characters")
	private String email;

	@NotBlank(message = "Password is mandatory")
	@Size(min = 8, max = 15, message = "Password must be between 8 and 15 characters")
	@Pattern(
		regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$",
		message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"
	)
	private String password;

	@NotBlank(message = "Name is mandatory")
	@Size(min = 2, max = 10, message = "Name must be between 2 and 10 characters")
	@Pattern(
		regexp = "^[a-zA-Z가-힣]*$",
		message = "Name must contain only letters"
	)
	private String name;

	@NotBlank(message = "Nickname is mandatory")
	@Size(min = 3, max = 13, message = "Nickname must be between 3 and 13 characters")
	@Pattern(
		regexp = "^[a-zA-Z가-힣0-9]*$",
		message = "Nickname must contain only letters and numbers"
	)
	private String nickname;

	@Size(min = 5, max = 100)
	private String introduce;

	private String image;

	private final String role = "";

	private final String adminToken = "";

}
