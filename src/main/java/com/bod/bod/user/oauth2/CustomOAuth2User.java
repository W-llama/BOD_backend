package com.bod.bod.user.oauth2;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * Custom OAuth2 User class that implements OAuth2User interface to handle OAuth2 user details.
 */
public class CustomOAuth2User implements OAuth2User {

	private final OAuth2UserInfo userInfo;
	private final Collection<GrantedAuthority> authorities;

	/**
	 * Constructor for CustomOAuth2User
	 *
	 * @param userInfo the OAuth2 user information
	 */
	public CustomOAuth2User(OAuth2UserInfo userInfo) {
		this.userInfo = userInfo;
		if (userInfo.getRole() != null) {
			this.authorities = Collections.singleton(new SimpleGrantedAuthority(userInfo.getRole().name()));
		} else {
			this.authorities = Collections.emptyList();
		}
	}

	/**
	 * Returns the attributes of the OAuth2 user.
	 *
	 * @return a map of user attributes
	 */
	@Override
	public Map<String, Object> getAttributes() {
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("email", userInfo.getEmail());
		attributes.put("name", userInfo.getName());
		return attributes;
	}

	/**
	 * Returns the authorities granted to the OAuth2 user.
	 *
	 * @return a collection of granted authorities
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	/**
	 * Returns the name of the OAuth2 user.
	 *
	 * @return the name of the user
	 */
	@Override
	public String getName() {
		return userInfo.getName();
	}

	/**
	 * Returns the username of the OAuth2 user.
	 *
	 * @return the username of the user
	 */
	public String getUsername() {
		return userInfo.getUsername();
	}
}
