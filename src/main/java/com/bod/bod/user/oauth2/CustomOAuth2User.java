package com.bod.bod.user.oauth2;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;


public class CustomOAuth2User implements OAuth2User {

	private final OAuth2UserInfo userInfo;
	private final Collection<GrantedAuthority> authorities;

	public CustomOAuth2User(OAuth2UserInfo userInfo) {
		this.userInfo = userInfo;
		if (userInfo.getRole() != null) {
			this.authorities = Collections.singleton(new SimpleGrantedAuthority(userInfo.getRole().name()));
		} else {
			this.authorities = Collections.emptyList();
		}
	}

	@Override
	public Map<String, Object> getAttributes() {
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("email", userInfo.getEmail());
		attributes.put("name", userInfo.getName());
		return attributes;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getName() {
		return userInfo.getName();
	}

	public String getUsername() {
		return userInfo.getUsername();
	}

	public String getNickname() {
		return userInfo.getNickname();
	}

	public String getProfileImage() {
		return userInfo.getProfileImage();
	}
}
