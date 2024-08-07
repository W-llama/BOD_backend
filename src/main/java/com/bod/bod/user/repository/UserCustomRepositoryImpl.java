//package com.bod.bod.user.repository;
//
//import com.bod.bod.user.dto.PointRankingResponseDto;
//import com.bod.bod.user.entity.QUser;
//import com.querydsl.core.types.Projections;
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import java.util.List;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Repository;
//
//@Repository
//@RequiredArgsConstructor
//public class UserCustomRepositoryImpl implements UserCustomRepository{
//
//  private final JPAQueryFactory queryFactory;
//
//  public List<PointRankingResponseDto> getPointRankingTop5List() {
//	QUser user = QUser.user;
//
//	List<PointRankingResponseDto> rankingPointList = queryFactory
//		.select(Projections.constructor(PointRankingResponseDto.class,
//			user.nickname,
//			user.point))
//		.from(user)
//		.orderBy(user.point.desc())
//		.limit(10)
//		.fetch();
//
//	int rank = 1;
//	long beforePoint = 0;
//	int count = 0;
//
//	for (PointRankingResponseDto dto : rankingPointList) {
//	  if (dto.getPoint() != beforePoint) {
//		rank = count + 1;
//	  }
//	  dto.setRank(rank);
//	  beforePoint = dto.getPoint();
//
//	  count++;
//	  if (rank == 5 && count >= 5) {
//		break;
//	  }
//	}
//	return rankingPointList.stream().limit(5).toList();
//  }
//}
