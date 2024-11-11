package com.example.my_bank_backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.my_bank_backend.domain.account.Account;
import com.example.my_bank_backend.domain.card.Card;

public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<Card> findByCardNumberAndAccount(String cardNumber, Account account);

    Optional<Card> findCardByAccountCpf(String accountCpf);

    List<Card> findCardsByAccountCpf(String accountCpf);
    
 @Query("SELECT c FROM Card c WHERE c.cardName = :cardName AND c.account.cpf = :accountCpf")
    List<Card> findByCardName(String cardName, String accountCpf);

    Optional<Card> findByCardNameAndAccountAndIsActive(String cardName, Account account, Boolean isActive);

}