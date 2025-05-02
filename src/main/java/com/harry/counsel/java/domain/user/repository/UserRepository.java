package com.harry.counsel.java.domain.user.repository;

import com.harry.counsel.java.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findBySocialIdAndProvider(String socialId, String provider);
    Optional<User> findByEmail(String email);
}