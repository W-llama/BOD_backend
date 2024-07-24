package com.bod.bod.verification.repository;

import com.bod.bod.verification.dto.VerificationWithUserResponseDto;
import java.util.List;

public interface VerificationCustomRepository {

  List<VerificationWithUserResponseDto> findVerificationWithUserByChallengeId(int page, Long challengeId);

}
