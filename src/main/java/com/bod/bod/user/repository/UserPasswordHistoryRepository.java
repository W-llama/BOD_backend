package com.bod.bod.user.repository;

import com.bod.bod.user.entity.User;
import com.bod.bod.user.entity.UserPasswordHistory;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPasswordHistoryRepository extends JpaRepository<UserPasswordHistory, Long> {
	List<UserPasswordHistory> findTop3ByUserOrderByChangedAtDesc(User user);

	UserPasswordHistory findByUserIdAndChangedAt(Long id, LocalDateTime changedAt);
}
