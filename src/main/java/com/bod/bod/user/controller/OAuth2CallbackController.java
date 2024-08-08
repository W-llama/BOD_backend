package com.bod.bod.user.controller;

import com.bod.bod.user.service.CustomOAuth2UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class OAuth2CallbackController {

	private static final Logger logger = Logger.getLogger(OAuth2CallbackController.class.getName());

	@Value("${spring.security.oauth2.client.registration.naver.client-id}")
	private String clientId;

	@Value("${spring.security.oauth2.client.registration.naver.client-secret}")
	private String clientSecret;

	@Value("${spring.security.oauth2.client.registration.naver.redirect-uri}")
	private String redirectUri;

	private final CustomOAuth2UserServiceImpl customOAuth2UserService;

	@PostMapping("/naver")
	public ResponseEntity<String> naverCallback(
		@RequestBody Map<String, String> params
	) {
		String code = params.get("code");
		String state = params.get("state");

		if (code == null || state == null) {
			return ResponseEntity.badRequest().body("Missing required parameters");
		}

		String tokenUrl = "https://nid.naver.com/oauth2.0/token?grant_type=authorization_code"
			+ "&client_id=" + clientId
			+ "&client_secret=" + clientSecret
			+ "&redirect_uri=" + redirectUri
			+ "&code=" + code
			+ "&state=" + state;

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.getForEntity(tokenUrl, String.class);

		if (response.getStatusCode() != HttpStatus.OK) {
			return ResponseEntity.status(response.getStatusCode()).body("Failed to get access token from Naver.");
		}

		String accessToken = customOAuth2UserService.extractAccessToken(response.getBody());
		String userInfoUrl = "https://openapi.naver.com/v1/nid/me";

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + accessToken);
		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<String> userInfoResponse = restTemplate.exchange(userInfoUrl, HttpMethod.GET, entity, String.class);

		if (userInfoResponse.getStatusCode() != HttpStatus.OK) {
			return ResponseEntity.status(userInfoResponse.getStatusCode()).body("Failed to get user info from Naver.");
		}

		String jwtToken = customOAuth2UserService.processNaverLogin(userInfoResponse.getBody());

		if (jwtToken == null) {
			throw new RuntimeException("Failed to process Naver login");
		}

		return ResponseEntity.ok(jwtToken);
	}
}
