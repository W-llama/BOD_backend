package com.bod.bod.challenge.repository;

import com.bod.bod.challenge.dto.ChallengeSummaryResponseDto;
import java.util.List;

public interface ChallengeCustomRepository {

  List<ChallengeSummaryResponseDto> findTop10ChallengesByUserchallenges();

}
