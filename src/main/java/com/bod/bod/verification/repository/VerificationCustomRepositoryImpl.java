package com.bod.bod.verification.repository;

import com.bod.bod.challenge.entity.QChallenge;
import com.bod.bod.user.entity.User;
import com.bod.bod.verification.dto.VerificationTop3UserResponseDto;
import com.bod.bod.verification.dto.VerificationWithChallengeResponseDto;
import com.bod.bod.verification.dto.VerificationWithUserResponseDto;
import com.bod.bod.verification.entity.QVerification;
import com.bod.bod.verification.entity.Status;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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

  public Page<VerificationWithChallengeResponseDto> getVerificationsByUser(Pageable pageable, User user) {
	QVerification verification = QVerification.verification;
	QChallenge challenge = QChallenge.challenge;

	JPAQuery<VerificationWithChallengeResponseDto> verificationList = queryFactory
		.select(Projections.constructor(VerificationWithChallengeResponseDto.class,
			verification.id,
			challenge.title,
			verification.status,
			verification.createdAt))
		.from(verification)
		.leftJoin(challenge)
		.on(verification.challenge.id.eq(challenge.id))
		.where(verification.user.eq(user))
		.offset(pageable.getOffset())
		.limit(pageable.getPageSize());

  	List<VerificationWithChallengeResponseDto> responseDto = verificationList.fetch();

	JPAQuery<Long> vericationCount = queryFactory
		.select(verification.count())
		.from(verification)
		.where(verification.user.eq(user));

	long totalElements = vericationCount.fetchOne();

	return new PageImpl<>(responseDto, pageable, totalElements);
  }

}
