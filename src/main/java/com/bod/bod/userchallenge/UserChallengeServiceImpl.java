package com.bod.bod.userchallenge;

import com.bod.bod.challenge.entity.ConditionStatus;
import com.bod.bod.user.entity.User;
import com.bod.bod.userchallenge.repository.UserChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserChallengeServiceImpl implements UserChallengeService {

	private final UserChallengeRepository userChallengeRepository;

	@Override
	public Long getMyChallengeCount(User user) {
		return userChallengeRepository.countAllByUser(user);
	}

	@Override
	public long getMyCompletedChallengesCount(User user) {
		return userChallengeRepository.countAllByUserAndChallengeConditionStatus(user, ConditionStatus.COMPLETE);
	}

}
