package com.bod.bod.challenge.repository;

import com.bod.bod.challenge.entity.Category;
import com.bod.bod.challenge.entity.Challenge;
import com.bod.bod.global.exception.ErrorCode;
import com.bod.bod.global.exception.GlobalException;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

public interface ChallengeRepository extends JpaRepository<Challenge, Long>, ChallengeCustomRepository {

  default Challenge findChallengeById(Long challengeId) {
	return findById(challengeId).orElseThrow(
		() -> new GlobalException(ErrorCode.NOT_FOUND_CHALLENGE));
  }

  Page<Challenge> findAll(Pageable pageable);

  Page<Challenge> findByCategory(Category category, Pageable pageable);

  Page<Challenge> findAllByOrderByCreatedAtAsc(Pageable pageable);

  Page<Challenge> findByTitleContaining(String title, Pageable pageable);

  @Lock(LockModeType.PESSIMISTIC_READ)
  @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")})  // timeout 설정 (3초)
  @Query("select c from Challenge c where c.id in :id")
  Optional<Challenge> findByIdWithPessimisticLock(Long id);

  @Query("SELECT COUNT(c) FROM Challenge c")
  long countAllChallenges();

  @Query("SELECT COUNT(c) FROM Challenge c WHERE c.conditionStatus = com.bod.bod.challenge.entity.ConditionStatus.BEFORE")
  long countBeforeChallenges();

  @Query("SELECT COUNT(c) FROM Challenge c WHERE c.conditionStatus = com.bod.bod.challenge.entity.ConditionStatus.TODO")
  long countTodoChallenges();

  @Query("SELECT COUNT(c) FROM Challenge c WHERE c.conditionStatus = com.bod.bod.challenge.entity.ConditionStatus.COMPLETE")
  long countCompleteChallenges();

}
