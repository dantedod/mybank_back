package com.example.my_bank_backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.my_bank_backend.domain.user.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByCpf(String cpf);
}
