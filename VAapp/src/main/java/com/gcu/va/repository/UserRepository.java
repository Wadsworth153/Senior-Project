package com.gcu.va.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.gcu.va.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

	List<User> findByUsernameContaining(String username);
}
