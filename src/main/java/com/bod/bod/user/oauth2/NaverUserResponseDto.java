package com.bod.bod.user.oauth2;

import java.util.Map;

public class NaverUserResponseDto implements OAuth2ResponseDto {

	private final Map<String, Object> attributes;

	public NaverUserResponseDto(Map<String, Object> attributes) {
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

	@SuppressWarnings("unchecked")
	private String getAttributeValue(String key) {
		Map<String, Object> response = (Map<String, Object>) attributes.get("response");
		if (response != null) {
			Object value = response.get(key);
			return value != null ? value.toString() : null;
		}
		return null;
	}

}
