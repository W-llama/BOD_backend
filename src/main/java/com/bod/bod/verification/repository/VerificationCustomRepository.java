package com.bod.bod.verification.repository;

import com.bod.bod.user.entity.User;
import com.bod.bod.verification.dto.VerificationTop3UserResponseDto;
import com.bod.bod.verification.dto.VerificationWithChallengeResponseDto;
import com.bod.bod.verification.dto.VerificationWithUserResponseDto;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VerificationCustomRepository {

  List<VerificationWithUserResponseDto> findVerificationWithUserByChallengeId(int page, Long challengeId);
  List<VerificationTop3UserResponseDto> getTop3VerificationUsers(Long challengeId);
  Page<VerificationWithChallengeResponseDto> getVerificationsByUser(Pageable pageable, User user);
}
