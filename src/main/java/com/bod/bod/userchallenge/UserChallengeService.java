package com.bod.bod.userchallenge;


import com.bod.bod.user.entity.User;

public interface UserChallengeService {

	Long getMyChallengeCount(User user);

	long getMyCompletedChallengesCount(User user);
}
