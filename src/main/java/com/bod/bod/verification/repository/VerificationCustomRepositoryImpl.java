package com.bod.bod.verification.repository;

import com.bod.bod.challenge.entity.QChallenge;
import com.bod.bod.verification.dto.VerificationTop3UserResponseDto;
import com.bod.bod.verification.dto.VerificationWithUserResponseDto;
import com.bod.bod.verification.entity.QVerification;
import com.bod.bod.verification.entity.Status;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class VerificationCustomRepositoryImpl implements VerificationCustomRepository {

  private final JPAQueryFactory queryFactory;

  private int PAGE_SIZE = 6;

  public List<VerificationWithUserResponseDto> findVerificationWithUserByChallengeId(int page, Long challengeId) {
	QVerification verification = QVerification.verification;
	QChallenge challenge = QChallenge.challenge;
	Pageable pageable = PageRequest.of(page, PAGE_SIZE);

	List<VerificationWithUserResponseDto> verificationList = queryFactory
		.select(Projections.constructor(VerificationWithUserResponseDto.class, verification))
		.from(verification)
		.where(verification.challenge.id.eq(challengeId))
		.orderBy(verification.createdAt.desc())
		.limit(PAGE_SIZE)
		.fetch();

	return verificationList;
  }

  public List<VerificationTop3UserResponseDto> getTop3VerificationUsers(Long challengeId) {
	QVerification verification = QVerification.verification;
	QChallenge challenge = QChallenge.challenge;

	List<VerificationTop3UserResponseDto> top3VerificationUserList = queryFactory
		.select(Projections.constructor(VerificationTop3UserResponseDto.class, verification))
		.from(verification)
		.where(verification.challenge.id.eq(challengeId).and(verification.status.eq(Status.APPROVE)))
		.groupBy(verification.user.id, verification.user.name, verification.user.nickname)
		.orderBy(verification.user.id.count().desc())
		.limit(3)
		.fetch();
	return top3VerificationUserList;
  }
}
