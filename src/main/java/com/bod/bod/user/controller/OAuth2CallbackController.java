package com.bod.bod.user.controller;

import com.bod.bod.user.service.CustomOAuth2UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class OAuth2CallbackController {

	@Value("${spring.security.oauth2.client.registration.naver.client-id}")
	private String clientId;

	@Value("${spring.security.oauth2.client.registration.naver.client-secret}")
	private String clientSecret;

	@Value("${spring.security.oauth2.client.registration.naver.redirect-uri}")
	private String redirectUri;

	private final CustomOAuth2UserServiceImpl customOAuth2UserService;

	@PostMapping("/naver")
	public ResponseEntity<String> naverCallback(
		@RequestParam String code,
		@RequestParam String state
	) {

		String tokenUrl = "https://nid.naver.com/oauth2.0/token?grant_type=authorization_code"
			+ "&client_id=" + clientId
			+ "&client_secret=" + clientSecret
			+ "&redirect_uri=" + redirectUri
			+ "&code=" + code
			+ "&state=" + state;

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.getForEntity(tokenUrl, String.class);

		String accessToken = customOAuth2UserService.extractAccessToken(response.getBody());
		String userInfoUrl = "https://openapi.naver.com/v1/nid/me";

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + accessToken);
		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<String> userInfoResponse = restTemplate.exchange(userInfoUrl, HttpMethod.GET, entity, String.class);
		String jwtToken = customOAuth2UserService.processNaverLogin(userInfoResponse.getBody());

		return ResponseEntity.ok(jwtToken);
	}
}
