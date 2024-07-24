package com.bod.bod.verification.repository;

import com.bod.bod.verification.entity.Verification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationRepository extends JpaRepository<Verification, Long> {

    Page<Verification> findByChallengeId(Long challengeId, Pageable pageable);
}
