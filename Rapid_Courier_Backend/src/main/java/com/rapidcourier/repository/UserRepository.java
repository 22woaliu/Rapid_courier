package com.rapidcourier.repository;

import com.rapidcourier.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsernameAndPassword(String username, String password);
    Optional<User> findByUsernameAndPasswordAndRole(String username, String password, String role);
    Optional<User> findByPhoneNumberAndPasswordAndRole(String phoneNumber, String password, String role);
    Optional<User> findByPhoneNumber(String phoneNumber);
}
