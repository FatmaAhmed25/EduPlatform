package com.edu.eduplatform.repos;

import com.edu.eduplatform.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String username);
    boolean existsByEmail(String email);

}
