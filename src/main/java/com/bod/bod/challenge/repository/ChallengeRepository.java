package com.bod.bod.challenge.repository;

import com.bod.bod.challenge.entity.Challenge;
import com.bod.bod.global.exception.ErrorCode;
import com.bod.bod.global.exception.GlobalException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeRepository extends JpaRepository<Challenge, Long>, ChallengeCustomRepository {

    default Challenge findChallengeById(Long challengeId) {
        return findById(challengeId).orElseThrow(
            () -> new GlobalException(ErrorCode.NOT_FOUND_CHALLENGE));
    }

    Page<Challenge> findAll(Pageable pageable);

}
