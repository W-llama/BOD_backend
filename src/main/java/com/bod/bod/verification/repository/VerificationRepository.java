package com.bod.bod.verification.repository;

import com.bod.bod.challenge.entity.Challenge;
import com.bod.bod.global.exception.ErrorCode;
import com.bod.bod.global.exception.GlobalException;
import com.bod.bod.user.entity.User;
import com.bod.bod.verification.entity.Verification;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationRepository extends JpaRepository<Verification, Long>, VerificationCustomRepository {

    default Verification findVerificationById(Long verificationId) {
        return findById(verificationId).orElseThrow(
            () -> new GlobalException(ErrorCode.NOT_FOUND_VERIFICATION));
    }

    Page<Verification> findAllByChallengeId(long challengeId, Pageable pageable);

    boolean existsByChallengeIdAndUserAndCreatedAtBetween(
        Long challengeId,
        User user,
        LocalDateTime startOfDay,
        LocalDateTime endOfDay
    );

    int countByChallengeAndUser(Challenge challenge, User user);
}
