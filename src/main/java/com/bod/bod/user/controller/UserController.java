package com.bod.bod.user.controller;

import com.bod.bod.challenge.dto.ChallengeResponseDto;
import com.bod.bod.global.dto.CommonResponseDto;
import com.bod.bod.global.jwt.security.UserDetailsImpl;
import com.bod.bod.user.dto.EditPasswordRequestDto;
import com.bod.bod.user.dto.EditProfileRequestDto;
import com.bod.bod.user.dto.LoginRequestDto;
import com.bod.bod.user.dto.SignUpRequestDto;
import com.bod.bod.user.dto.UserResponseDto;
import com.bod.bod.user.service.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

	private final UserServiceImpl userService;

	@PostMapping("/signup")
	public ResponseEntity<CommonResponseDto<Void>> signUp(
		@RequestBody @Valid SignUpRequestDto signUpRequestDto
	) {
		userService.signUp(signUpRequestDto);
		return ResponseEntity.ok().body(new CommonResponseDto<>
			(HttpStatus.CREATED.value(), "회원가입이 완료되었습니다.", null));
	}

	@PostMapping("/login")
	public ResponseEntity<CommonResponseDto<Void>> login(
		@RequestBody @Valid LoginRequestDto loginRequestDto,
		HttpServletResponse response
	) {
		userService.login(loginRequestDto, response);
		return ResponseEntity.ok().body(new CommonResponseDto<>(
			HttpStatus.OK.value(), "로그인이 완료되었습니다.", null));
	}

	@DeleteMapping("/logout")
	public ResponseEntity<CommonResponseDto<Void>> logout(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		HttpServletRequest request,
		HttpServletResponse response
	) {
		userService.logout(request, response, userDetails.getUser());
		return ResponseEntity.ok().body(new CommonResponseDto<>(
			HttpStatus.NO_CONTENT.value(), "로그아웃이 완료되었습니다.", null));
	}

	@DeleteMapping("/withdraw")
	public ResponseEntity<CommonResponseDto<Void>> withdraw(
		@RequestParam String username,
		@RequestParam String password,
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		HttpServletResponse response
	) {
		userService.withdraw(username, password, userDetails.getUser(), response);
		return ResponseEntity.ok().body(new CommonResponseDto<>(
			HttpStatus.OK.value(), "회원탈퇴가 완료되었습니다.", null));
	}

	@PostMapping("/checkUserName")
	public ResponseEntity<CommonResponseDto<Void>> validateUsernameRequest(
		@RequestParam String username,
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		userService.validateUsernameRequest(username, userDetails.getUser());
		return ResponseEntity.ok().body(new CommonResponseDto<>(
			HttpStatus.OK.value(), "아이디가 일치합니다", null));
	}

	@GetMapping("/users/{userId}")
	public ResponseEntity<CommonResponseDto<UserResponseDto>> getUserprofile(
		@PathVariable(name = "userId") long userId
	) {
		UserResponseDto userResponseDto = userService.getUserprofile(userId);
		return ResponseEntity.ok().body(new CommonResponseDto<>(
			HttpStatus.OK.value(), "선택한 유저의 프로필 조회가 완료되었습니다.", userResponseDto));
	}

	@GetMapping("/users/profile")
	public ResponseEntity<CommonResponseDto<UserResponseDto>> getMyProfile(
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		UserResponseDto userResponseDto = userService.getMyProfile(userDetails.getUser());
		return ResponseEntity.ok().body(new CommonResponseDto<>(
			HttpStatus.OK.value(), "프로필 조회가 완료되었습니다.", userResponseDto));
	}

	@PutMapping("/users/profile")
	public ResponseEntity<CommonResponseDto<UserResponseDto>> editProfile(
		@RequestBody @Valid EditProfileRequestDto editProfileRequestDto,
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		UserResponseDto userResponseDto = userService.editProfile(editProfileRequestDto, userDetails.getUser());
		return ResponseEntity.ok().body(new CommonResponseDto<>(
			HttpStatus.OK.value(), "회원정보 수정이 완료되었습니다.", userResponseDto));
	}

	@PutMapping("/users/profile/password")
	public ResponseEntity<CommonResponseDto<UserResponseDto>> editPassword(
		@RequestBody @Valid EditPasswordRequestDto editPasswordRequestDto,
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		UserResponseDto userResponseDto = userService.editPassword(editPasswordRequestDto, userDetails.getUser());
		return ResponseEntity.ok().body(new CommonResponseDto<>(
			HttpStatus.OK.value(), "비밀번호 수정이 완료되었습니다.", userResponseDto));
	}

	@PostMapping("/users/validateNewPassword")
	public ResponseEntity<CommonResponseDto<UserResponseDto>> validateNewPassword(
		@RequestBody String newPassword,
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		userService.validateNewPassword(newPassword, userDetails.getUser());
		return ResponseEntity.ok().body(new CommonResponseDto<>(
			HttpStatus.OK.value(), "최근 사용한 3개의 비밀번호와 일치하지않습니다.", null));
	}

	@PutMapping("/users/profile/image")
	public ResponseEntity<CommonResponseDto<UserResponseDto>> editProfileImage(
		@RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		UserResponseDto userResponseDto = userService.editProfileImage(profileImage, userDetails.getUser());
		return ResponseEntity.ok().body(new CommonResponseDto<>(
			HttpStatus.OK.value(), "프로필 이미지 수정이 완료되었습니다.", userResponseDto));
	}

	@GetMapping("/users/profile/challenges")
	public ResponseEntity<CommonResponseDto<Map<String, Slice<ChallengeResponseDto>>>> getMyChallenges(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PageableDefault Pageable pageable
	) {
		Map<String, Slice<ChallengeResponseDto>> challengeSlices = userService.getMyChallenges(userDetails.getUser(), pageable);
		return ResponseEntity.ok().body(new CommonResponseDto<>(
			HttpStatus.OK.value(), "참여한 챌린지 목록 조회가 완료되었습니다.", challengeSlices));
	}
}
