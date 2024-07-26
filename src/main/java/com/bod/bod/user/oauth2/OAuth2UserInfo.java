package com.bod.bod.user.oauth2;

import com.bod.bod.user.entity.UserRole;
import com.bod.bod.user.entity.UserStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OAuth2UserInfo {

	private final String name;

	private final String username;

	private final String password;

	private final String nickname;

	private final String email;

	private final String profileImage;

	private final UserRole role;

	private final UserStatus userStatus;
}
