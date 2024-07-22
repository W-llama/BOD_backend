package com.bod.bod.user.oauth2;

import java.util.Map;

public class GoogleUserResponseDto implements OAuth2ResponseDto{

	private final Map<String, Object> attributes;

	public GoogleUserResponseDto(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	@Override
	public String getProvider() {
		return "google";
	}

	@Override
	public String getProviderId() {
		return getAttributeValue("sub");
	}

	@Override
	public String getEmail() {
		return getAttributeValue("email");
	}

	@Override
	public String getName() {
		return getAttributeValue("name");
	}

	private String getAttributeValue(String key) {
		Object value = attributes.get(key);
		return value != null ? value.toString() : null;
	}

}
