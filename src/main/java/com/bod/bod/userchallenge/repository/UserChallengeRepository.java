package com.bod.bod.userchallenge.repository;

import com.bod.bod.challenge.entity.Challenge;
import com.bod.bod.userchallenge.entity.UserChallenge;
import com.bod.bod.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserChallengeRepository extends JpaRepository<UserChallenge, Long> {

    Optional<UserChallenge> findByUserAndChallenge(User user, Challenge challenge);

    List<UserChallenge> findByUserIdAndChallengeId(long userId, long challengeId);

    List<UserChallenge> findByChallengeId(Long challenge);
}
