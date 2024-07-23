package com.bod.bod.user.dto;

import com.bod.bod.user.entity.User;
import lombok.Getter;

@Getter
public class UserResponseDto {

	private String email;

	private String name;

	public UserResponseDto(User user) {
		this.email = user.getEmail();
		this.name = user.getUsername();

	}
}
