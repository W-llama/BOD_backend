package com.bod.bod.challenge.repository;

import com.bod.bod.challenge.dto.ChallengeSummaryResponseDto;
import com.bod.bod.challenge.entity.Category;
import com.bod.bod.challenge.entity.QChallenge;
import com.bod.bod.userchallenge.entity.QUserChallenge;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChallengeCustomRepositoryImpl implements ChallengeCustomRepository {

  private final JPAQueryFactory queryFactory;

  private int PAGE_SIZE = 10;

  public List<ChallengeSummaryResponseDto> getChallengeListByCategory(int page, Category category) {
	QChallenge challenge = QChallenge.challenge;
	Pageable pageable = PageRequest.of(page, PAGE_SIZE);

	List<ChallengeSummaryResponseDto> challengeList = queryFactory
		.select(Projections.constructor(ChallengeSummaryResponseDto.class, challenge))
		.from(challenge)
		.where(challenge.category.eq(category))
		.orderBy(challenge.createdAt.desc())
		.offset(pageable.getOffset())
		.limit(pageable.getPageSize())
		.fetch();
	return challengeList;
  }

  public List<ChallengeSummaryResponseDto> getChallengeList(int page) {
	QChallenge challenge = QChallenge.challenge;
	Pageable pageable = PageRequest.of(page, PAGE_SIZE);

	List<ChallengeSummaryResponseDto> challengeList = queryFactory
		.select(Projections.constructor(ChallengeSummaryResponseDto.class, challenge))
		.from(challenge)
		.orderBy(challenge.createdAt.desc())
		.offset(pageable.getOffset())
		.limit(pageable.getPageSize())
		.fetch();
	return challengeList;
  }

  public List<ChallengeSummaryResponseDto> findTop10ChallengesByUserchallenges() {
	QChallenge challenge = QChallenge.challenge;
	QUserChallenge userChallenge = QUserChallenge.userChallenge;

	List<ChallengeSummaryResponseDto> top10ChallengeList = queryFactory
		.select((Projections.constructor(ChallengeSummaryResponseDto.class, challenge)))
		.from(userChallenge)
		.join(userChallenge.challenge, challenge)
		.groupBy(challenge)
		.orderBy(userChallenge.challenge.count().desc())
		.limit(10)
		.fetch();
	return top10ChallengeList;
  }


}
