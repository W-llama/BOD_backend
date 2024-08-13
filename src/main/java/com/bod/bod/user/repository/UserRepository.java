package com.bod.bod.user.repository;

import com.bod.bod.user.entity.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long>{

	Optional<User> findByUsername(String username);

	Optional<User> findByEmail(String email);

	Optional<User> findByNickname(String nickname);

	Page<User> findAllByOrderByCreatedAtAsc(Pageable pageable);

	@Query("SELECT COUNT(u) FROM User u")
	long countAllUsers();

	@Query("SELECT COUNT(u) FROM User u WHERE u.userStatus = com.bod.bod.user.entity.UserStatus.ACTIVE")
	long countActiveUsers();

	@Query("SELECT COUNT(u) FROM User u WHERE u.userStatus = com.bod.bod.user.entity.UserStatus.WITHDRAW")
	long countWithdrawUsers();

	@Query("SELECT COUNT(u) FROM User u WHERE u.userRole = com.bod.bod.user.entity.UserRole.ADMIN")
	long countAdminUsers();

	@Query("SELECT COUNT(u) FROM User u WHERE u.userRole = com.bod.bod.user.entity.UserRole.USER")
	long countUserUsers();


}
