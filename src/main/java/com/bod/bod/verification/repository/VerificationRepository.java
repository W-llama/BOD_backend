package com.bod.bod.verification.repository;

import com.bod.bod.global.exception.ErrorCode;
import com.bod.bod.global.exception.GlobalException;
import com.bod.bod.user.entity.User;
import com.bod.bod.verification.entity.Verification;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationRepository extends JpaRepository<Verification, Long>, VerificationCustomRepository {

    default Verification findVerificationById(Long verificationId) {
        return findById(verificationId).orElseThrow(
            () -> new GlobalException(ErrorCode.NOT_FOUND_VERIFICATION));
    }

    Page<Verification> findAllByChallengeId(long challengeId, Pageable pageable);

    List<Verification> findByCreatedAtBetweenAndUser(LocalDateTime startDateTime, LocalDateTime endDateTime, User user);
}
