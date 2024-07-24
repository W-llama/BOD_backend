package com.bod.bod.user.service;

import com.bod.bod.user.entity.User;
import com.bod.bod.user.entity.UserRole;
import com.bod.bod.user.entity.UserStatus;
import com.bod.bod.user.oauth2.CustomOAuth2User;
import com.bod.bod.user.oauth2.GoogleUserResponseDto;
import com.bod.bod.user.oauth2.NaverUserResponseDto;
import com.bod.bod.user.oauth2.OAuth2ResponseDto;
import com.bod.bod.user.oauth2.OAuth2UserInfo;
import com.bod.bod.user.repository.UserRepository;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserServiceImpl extends DefaultOAuth2UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public CustomOAuth2UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

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

		return optionalUser.map(user ->
			updateUser(user, oAuth2ResponseDto)).orElseGet(() ->
			createUser(oAuth2ResponseDto));
	}

	private OAuth2ResponseDto getOAuth2ResponseDto(String registrationId, OAuth2User oAuth2User) {
		return switch (registrationId) {
			case "naver" -> new NaverUserResponseDto(oAuth2User.getAttributes());
			case "google" -> new GoogleUserResponseDto(oAuth2User.getAttributes());
			default -> null;
		};
	}

	private OAuth2User updateUser(User existingUser, OAuth2ResponseDto oAuth2ResponseDto) {
		existingUser.changeEmail(oAuth2ResponseDto.getEmail());
		existingUser.changeName(oAuth2ResponseDto.getName());

		userRepository.save(existingUser);

		OAuth2UserInfo userInfo = OAuth2UserInfo.builder()
			.username(existingUser.getUsername())
			.password(existingUser.getPassword())
			.name(existingUser.getName())
			.nickname(existingUser.getNickname())
			.email(oAuth2ResponseDto.getEmail())
			.profileImage(oAuth2ResponseDto.getProfileImage())
			.role(UserRole.USER)
			.userStatus(UserStatus.ACTIVE)
			.build();

		return new CustomOAuth2User(userInfo);
	}

	private OAuth2User createUser(OAuth2ResponseDto oAuth2ResponseDto) {
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

		OAuth2UserInfo userInfo = OAuth2UserInfo.builder()
			.username(newUser.getUsername())
			.password(newUser.getPassword())
			.name(newUser.getName())
			.nickname(newUser.getEmail())
			.email(newUser.getEmail())
			.profileImage(newUser.getImage())
			.role(UserRole.USER)
			.userStatus(UserStatus.ACTIVE)
			.build();

		return new CustomOAuth2User(userInfo);
	}
}
