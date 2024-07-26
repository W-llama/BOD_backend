package com.bod.bod.user.oauth2;

import java.util.Map;

public class GoogleUserResponseDto implements OAuth2ResponseDto {

	private final Map<String, Object> attributes;

	public GoogleUserResponseDto(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	@Override
	public String getEmail() {
		return getAttributeValue("email");
	}

	@Override
	public String getName() {
		return getAttributeValue("name");
	}
	@Override
	public String getNickname() {
		return getAttributeValue("nickname");
	}

	@Override
	public String getProfileImage() {
		return getAttributeValue("profile_image");
	}

	private String getAttributeValue(String key) {
		Object value = attributes.get(key);
		return value != null ? value.toString() : null;
	}

}
