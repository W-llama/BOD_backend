package com.bod.bod.challenge.repository;

import com.bod.bod.challenge.entity.Challenge;
import com.bod.bod.challenge.entity.UserChallenge;
import com.bod.bod.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserChallengeRepository extends JpaRepository<UserChallenge, Long> {

    Optional<UserChallenge> findByUserAndChallenge(User user, Challenge challenge);

    List<UserChallenge> findByUserIdAndChallengeId(long userId, long challengeId);

}
