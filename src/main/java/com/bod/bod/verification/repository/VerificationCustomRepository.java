package com.bod.bod.verification.repository;

import com.bod.bod.verification.dto.VerificationTop3UserResponseDto;
import com.bod.bod.verification.dto.VerificationWithUserResponseDto;
import java.util.List;

public interface VerificationCustomRepository {

  List<VerificationWithUserResponseDto> findVerificationWithUserByChallengeId(int page, Long challengeId);
  List<VerificationTop3UserResponseDto> getTop3VerificationUsers(Long challengeId);
}
