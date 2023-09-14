package com.klasha.assessment.repository;


import com.klasha.assessment.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
	 Optional<User> findByUsername(String username);
	// boolean existsByUsername(String username);
}