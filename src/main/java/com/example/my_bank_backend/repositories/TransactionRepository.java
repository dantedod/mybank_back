package com.example.my_bank_backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.my_bank_backend.domain.account.Account;
import com.example.my_bank_backend.domain.transaction.Transaction;

public interface TransactionRepository  extends JpaRepository<Transaction, Long>{
    List<Transaction> findBySenderAccountIdOrReceiverAccountId(Account senderAccount, Account receiverAccount);
}
