package com.bod.bod.user.oauth2;

import com.bod.bod.user.entity.UserRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OAuth2UserInfo {
	private final UserRole role;
	private final String name;
	private final String username;
	private final String email;
}
