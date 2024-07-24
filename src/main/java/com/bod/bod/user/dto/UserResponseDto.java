package com.bod.bod.user.dto;

import com.bod.bod.user.entity.User;
import lombok.Getter;

@Getter
public class UserResponseDto {

	private final String nickname;
	private final String introduce;
	private final String image;

	public UserResponseDto(User user) {
		this.nickname = user.getNickname();
		this.introduce = user.getIntroduce();
		this.image = user.getImage();
	}
}
