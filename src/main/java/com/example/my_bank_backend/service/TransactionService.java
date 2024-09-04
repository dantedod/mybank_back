package com.example.my_bank_backend.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.my_bank_backend.domain.account.Account;
import com.example.my_bank_backend.domain.enums.TransactionEnum;
import com.example.my_bank_backend.domain.transaction.Transaction;
import com.example.my_bank_backend.dto.TransactionResponseDto;
import com.example.my_bank_backend.repositories.AccountRepository;
import com.example.my_bank_backend.repositories.TransactionRepository;

import jakarta.transaction.Transactional;

@Service
public class TransactionService {

    private AccountRepository accountRepository;

    private TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public TransactionResponseDto processTransaction(String cpfSender, String cpfReceiver, Double amount,
            String paymentDescription, TransactionEnum transactionType) {

        Account senderAccount = accountRepository.findByCpf(cpfSender)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));

        Account receiverAccount = accountRepository.findByCpf(cpfReceiver)
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        if (senderAccount.getAccountValue() < amount) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        senderAccount.setAccountValue(senderAccount.getAccountValue() - amount);
        receiverAccount.setAccountValue(receiverAccount.getAccountValue() + amount);

        accountRepository.save(senderAccount);
        accountRepository.save(receiverAccount);

        Transaction transaction = new Transaction();
        transaction.setSenderAccountId(senderAccount);
        transaction.setReceiverAccountId(receiverAccount);
        transaction.setAmount(amount);
        transaction.setPaymentDescription(paymentDescription);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setTransactionType(transactionType);

        Transaction savedTransaction = transactionRepository.save(transaction);

        return new TransactionResponseDto(
                savedTransaction.getId(),
                senderAccount.getCpf(),
                senderAccount.getUser().getName(),
                receiverAccount.getCpf(),
                receiverAccount.getUser().getName(),
                savedTransaction.getAmount(),
                savedTransaction.getPaymentDescription(),
                savedTransaction.getTransactionDate(),
                savedTransaction.getTransactionType());
    }
}
