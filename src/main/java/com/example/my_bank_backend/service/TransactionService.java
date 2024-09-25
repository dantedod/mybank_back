package com.example.my_bank_backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.my_bank_backend.domain.account.Account;
import com.example.my_bank_backend.domain.enums.TransactionEnum;
import com.example.my_bank_backend.domain.transaction.Transaction;
import com.example.my_bank_backend.dto.TransactionResponseDto;
import com.example.my_bank_backend.repositories.AccountRepository;
import com.example.my_bank_backend.repositories.TransactionRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

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

    public ResponseEntity<List<TransactionResponseDto>> getAllTransactionsByCpf(String cpf) {
        Optional<Account> optAccount = accountRepository.findByCpf(cpf);

        if (optAccount.isPresent()) {
            Account account = optAccount.get();

            List<Transaction> transactions = transactionRepository.findBySenderAccountIdOrReceiverAccountId(account,
                    account);

            if (transactions.isEmpty()) {
                return ResponseEntity.noContent().build();
            } else {
                List<TransactionResponseDto> responseDtos = transactions.stream()
                        .map(tx -> new TransactionResponseDto(
                                tx.getId(),
                                tx.getSenderAccountCpf(),
                                tx.getSenderAccountId().getUser().getName(),
                                tx.getReceiverAccountCpf(),
                                tx.getReceiverAccountId().getUser().getName(),
                                tx.getAmount(),
                                tx.getPaymentDescription(),
                                tx.getTransactionDate(),
                                tx.getTransactionType()))
                        .collect(Collectors.toList());
                return ResponseEntity.ok(responseDtos);
            }
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    public ResponseEntity<Transaction> getTransactionByCpf(Long id) {

        Optional<Transaction> transaction = transactionRepository.findById(id);

        if (transaction.isPresent()) {
            return ResponseEntity.ok(transaction.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
