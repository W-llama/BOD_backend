package com.bod.bod.user.dto;

import com.bod.bod.user.entity.User;
import lombok.Getter;

@Getter
public class UserResponseDto {

	private final String image;
	private final String nickname;
	private final String introduce;

	public UserResponseDto(User user) {
		this.image = user.getImage();
		this.nickname = user.getNickname();
		this.introduce = user.getIntroduce();
	}
}
