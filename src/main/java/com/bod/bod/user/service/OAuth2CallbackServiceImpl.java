package com.bod.bod.user.service;

import com.bod.bod.global.exception.ErrorCode;
import com.bod.bod.global.exception.GlobalException;
import com.bod.bod.global.jwt.JwtUtil;
import com.bod.bod.user.entity.User;
import com.bod.bod.user.entity.UserRole;
import com.bod.bod.user.entity.UserStatus;
import com.bod.bod.user.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class OAuth2CallbackServiceImpl {

	private final JwtUtil jwtUtil;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final RefreshTokenService refreshTokenService;

	@Value("${spring.security.oauth2.client.registration.naver.client-id}")
	private String clientId;

	@Value("${spring.security.oauth2.client.registration.naver.client-secret}")
	private String clientSecret;

	@Value("${spring.security.oauth2.client.registration.naver.redirect-uri}")
	private String redirectUri;

	@Value("${spring.security.oauth2.client.provider.naver.user-info-uri}")
	private String userInfoUrl;

	@Value("${jwt.refresh-expire-time}")
	private int refreshTokenExpireTime; // 초 단위

	public void handleNaverLogin(Map<String, String> params, HttpServletResponse httpServletResponse) {
		String code = params.get("code");
		String state = params.get("state");
		validateStatusCodeAndState(code, state);

		String tokenUrl = buildTokenUrl(code, state);
		String accessToken = getAccessToken(tokenUrl);
		ResponseEntity<String> userInfoResponse = getUserInfo(accessToken);

		checkStatusCode(userInfoResponse);
		processNaverLogin(userInfoResponse.getBody(), httpServletResponse);
	}

	private String buildTokenUrl(String code, String state) {
		return "https://nid.naver.com/oauth2.0/token?grant_type=authorization_code"
			+ "&client_id=" + clientId
			+ "&client_secret=" + clientSecret
			+ "&redirect_uri=" + redirectUri
			+ "&code=" + code
			+ "&state=" + state;
	}

	private String getAccessToken(String tokenUrl) {
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.getForEntity(tokenUrl, String.class);
		checkNaverAccessCode(response);
		return extractAccessToken(response.getBody());
	}

	private ResponseEntity<String> getUserInfo(String accessToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.set(JwtUtil.AUTHORIZATION_HEADER, JwtUtil.BEARER_PREFIX + accessToken);
		HttpEntity<String> entity = new HttpEntity<>(headers);

		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.exchange(userInfoUrl, HttpMethod.GET, entity, String.class);
	}

	public String extractAccessToken(String responseBody) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(responseBody);
			return rootNode.path("access_token").asText();
		} catch (Exception e) {
			throw new GlobalException(ErrorCode.FAILED_TO_EXTRACT_ACCESS_TOKEN);
		}
	}

	public void processNaverLogin(String userInfoResponseBody, HttpServletResponse httpServletResponse) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(userInfoResponseBody);
			String email = rootNode.path("response").path("email").asText();
			String name = rootNode.path("response").path("name").asText();
			String nickname = rootNode.path("response").path("nickname").asText();

			Optional<User> optionalUser = userRepository.findByEmail(email);
			User user = optionalUser.orElseGet(() -> createUser(email, name, nickname));

			jwtUtil.issueTokens(user, httpServletResponse, refreshTokenService, refreshTokenExpireTime);
		} catch (Exception e) {
			throw new GlobalException(ErrorCode.FAILED_TO_PROCESS_NAVER_LOGIN);
		}
	}

	private User createUser(String email, String name, String nickname) {
		User newUser = User.builder()
			.email(email)
			.username(email)
			.password(passwordEncoder.encode("temporary_password"))
			.name(name)
			.nickname(nickname)
			.userRole(UserRole.USER)
			.userStatus(UserStatus.ACTIVE)
			.build();
		return userRepository.save(newUser);
	}

	private void validateStatusCodeAndState(String code, String state) {
		if (code == null || state == null) {
			throw new GlobalException(ErrorCode.MISSING_REQUIRED_PARAMETERS);
		}
	}

	private void checkNaverAccessCode(ResponseEntity<String> response) {
		if (response.getStatusCode() != HttpStatus.OK) {
			throw new GlobalException(ErrorCode.FAILED_TO_GET_ACCESS_TOKEN_FROM_NAVER);
		}
	}

	private void checkStatusCode(ResponseEntity<String> userInfoResponse) {
		if (userInfoResponse.getStatusCode() != HttpStatus.OK) {
			throw new GlobalException(ErrorCode.FAILED_TO_GET_USER_INFO_FROM_NAVER);
		}
	}
}
