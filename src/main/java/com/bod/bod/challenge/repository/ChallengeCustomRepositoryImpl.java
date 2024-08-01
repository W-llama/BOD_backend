package com.bod.bod.challenge.repository;

import com.bod.bod.challenge.dto.ChallengeSummaryResponseDto;
import com.bod.bod.challenge.entity.QChallenge;
import com.bod.bod.userchallenge.entity.QUserChallenge;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChallengeCustomRepositoryImpl implements ChallengeCustomRepository {

  private final JPAQueryFactory queryFactory;

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
