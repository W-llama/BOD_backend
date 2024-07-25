package com.bod.bod.verification.repository;

import com.bod.bod.admin.dto.AdminVerificationGetResponse;
import com.bod.bod.global.exception.ErrorCode;
import com.bod.bod.global.exception.GlobalException;
import com.bod.bod.verification.entity.Verification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationRepository extends JpaRepository<Verification, Long>, VerificationCustomRepository {

  default Verification findVerificationById(Long verificationId) {
	return findById(verificationId).orElseThrow(
		()-> new GlobalException(ErrorCode.NOT_FOUND_VERIFICATION));
  }

    Page<AdminVerificationGetResponse> findAllByChallengeId(long challengeId, Pageable pageable);
}
