package com.example.my_bank_backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.my_bank_backend.domain.card.Card;
import com.example.my_bank_backend.domain.transaction.Transaction;

public interface TransactionRepository  extends JpaRepository<Transaction, Long>{
    List<Transaction> findByAccountCpf(String cpf);

    List<Transaction> findByCard(Card card);
}
