package com.bod.bod.userchallenge;

import com.bod.bod.global.dto.CommonResponseDto;
import com.bod.bod.global.jwt.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserChallengeController {

	private final UserChallengeServiceImpl userChallengeService;


	@GetMapping("/challenges/count")
	public ResponseEntity<CommonResponseDto<Long>> getMyChallengeCount(
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		long challengeCount = userChallengeService.getMyChallengeCount(userDetails.getUser());
		return ResponseEntity.ok().body(new CommonResponseDto<>(
			HttpStatus.OK.value(), "참여한 챌린지의 갯수 조회가 완료되었습니다.", challengeCount));
	}

	@GetMapping("/challenges/completed/count")
	public ResponseEntity<CommonResponseDto<Long>> getMyCompletedChallengesCount(
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		long completedChallengesCount = userChallengeService.getMyCompletedChallengesCount(userDetails.getUser());

		return ResponseEntity.ok().body(new CommonResponseDto<>(
			HttpStatus.OK.value(), "완료된 챌린지의 갯수 조회가 완료되었습니다.", completedChallengesCount));
	}
}
