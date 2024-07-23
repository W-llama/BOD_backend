package com.bod.bod.challenge.repository;

import com.bod.bod.challenge.dto.ChallengeSummaryResponseDto;
import com.bod.bod.challenge.entity.Category;
import java.util.List;

public interface ChallengeCustomRepository {

  List<ChallengeSummaryResponseDto> getChallengeListByCategory(int page, Category category);

}
