package com.example.my_bank_backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.my_bank_backend.domain.account.Account;
import com.example.my_bank_backend.domain.card.Card;

public interface CardRepository extends JpaRepository<Card, Long > {
    Optional<Card> findByCardNumberAndAccount(String cardNumber, Account account);
}