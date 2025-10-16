package com.example.StudentRbacApplication.repo;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.StudentRbacApplication.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    
}
