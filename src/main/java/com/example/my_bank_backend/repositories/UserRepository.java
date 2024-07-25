package com.example.my_bank_backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.my_bank_backend.domain.user.User;

public interface UserRepository extends JpaRepository<User, String>{

    Optional<User> findByEmail(String email);
}
