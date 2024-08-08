package com.bod.bod.user.service;

import com.bod.bod.global.jwt.JwtUtil;
import com.bod.bod.user.entity.User;
import com.bod.bod.user.entity.UserRole;
import com.bod.bod.user.entity.UserStatus;
import com.bod.bod.user.oauth2.CustomOAuth2User;
import com.bod.bod.user.oauth2.OAuth2ResponseDto;
import com.bod.bod.user.repository.UserRepository;
import com.bod.bod.user.oauth2.NaverUserResponseDto;
import com.bod.bod.user.oauth2.GoogleUserResponseDto;
import com.bod.bod.user.oauth2.OAuth2UserInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserServiceImpl extends DefaultOAuth2UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest);

		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		OAuth2ResponseDto oAuth2ResponseDto = getOAuth2ResponseDto(registrationId, oAuth2User);
		if (oAuth2ResponseDto == null) {
			throw new OAuth2AuthenticationException("Unsupported provider: " + registrationId);
		}

		String email = oAuth2ResponseDto.getEmail();
		Optional<User> optionalUser = userRepository.findByEmail(email);

		OAuth2UserInfo userInfo;
		if (optionalUser.isPresent()) {
			userInfo = updateUser(optionalUser.get(), oAuth2ResponseDto);
		} else {
			userInfo = createUser(oAuth2ResponseDto);
		}

		return new CustomOAuth2User(userInfo);
	}

	private OAuth2ResponseDto getOAuth2ResponseDto(String registrationId, OAuth2User oAuth2User) {
		return switch (registrationId) {
			case "naver" -> new NaverUserResponseDto(oAuth2User.getAttributes());
			case "google" -> new GoogleUserResponseDto(oAuth2User.getAttributes());
			default -> null;
		};
	}

	private OAuth2UserInfo updateUser(User existingUser, OAuth2ResponseDto oAuth2ResponseDto) {
		existingUser.changeEmail(oAuth2ResponseDto.getEmail());
		existingUser.changeName(oAuth2ResponseDto.getName());
		userRepository.save(existingUser);

		return OAuth2UserInfo.builder()
			.username(existingUser.getUsername())
			.password(existingUser.getPassword())
			.name(existingUser.getName())
			.nickname(existingUser.getNickname())
			.email(oAuth2ResponseDto.getEmail())
			.profileImage(oAuth2ResponseDto.getProfileImage())
			.role(UserRole.USER)
			.userStatus(UserStatus.ACTIVE)
			.build();
	}

	private OAuth2UserInfo createUser(OAuth2ResponseDto oAuth2ResponseDto) {
		User newUser = User.builder()
			.username(oAuth2ResponseDto.getEmail())
			.password(passwordEncoder.encode("temporary_password")) // 임시 비밀번호를 암호화
			.name(oAuth2ResponseDto.getName())
			.nickname(oAuth2ResponseDto.getEmail())
			.email(oAuth2ResponseDto.getEmail())
			.image(oAuth2ResponseDto.getProfileImage())
			.userRole(UserRole.USER)
			.userStatus(UserStatus.ACTIVE)
			.build();
		userRepository.save(newUser);

		return OAuth2UserInfo.builder()
			.username(newUser.getUsername())
			.password(newUser.getPassword())
			.name(newUser.getName())
			.nickname(newUser.getEmail())
			.email(newUser.getEmail())
			.profileImage(newUser.getImage())
			.role(UserRole.USER)
			.userStatus(UserStatus.ACTIVE)
			.build();
	}

	public String extractAccessToken(String responseBody) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(responseBody);
			return rootNode.path("access_token").asText();
		} catch (Exception e) {
			throw new RuntimeException("Failed to extract access token", e);
		}
	}

	public String processNaverLogin(String userInfoResponseBody) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(userInfoResponseBody);
			String email = rootNode.path("response").path("email").asText();
			String name = rootNode.path("response").path("name").asText();
			String nickname = rootNode.path("response").path("nickname").asText();
			Optional<User> optionalUser = userRepository.findByEmail(email);

			User user = optionalUser.orElseGet(() -> {
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
			});

			return jwtUtil.createAccessToken(user.getUsername(), user.getUserRole());
		} catch (Exception e) {
			throw new RuntimeException("Failed to process Naver login", e);
		}
	}
}
