package com.example.my_bank_backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.my_bank_backend.domain.account.Account;
import com.example.my_bank_backend.domain.card.Card;
import com.example.my_bank_backend.domain.transaction.Transaction;
import com.example.my_bank_backend.dto.TransactionResponseDto;
import com.example.my_bank_backend.repositories.AccountRepository;
import com.example.my_bank_backend.repositories.CardRepository;
import com.example.my_bank_backend.repositories.TransactionRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final CardRepository cardRepository;

    @Transactional
    public TransactionResponseDto processTransaction(Long accountId, Long cardId, Double amount,
            String paymentDescription) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Card not found"));

        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setCard(card);
        transaction.setAccount(account);
        transaction.setAmount(amount);
        transaction.setPaymentDescription(paymentDescription);
        transaction.setTransactionDate(LocalDateTime.now());

        Transaction savedTransaction = transactionRepository.save(transaction);

        return new TransactionResponseDto(
                savedTransaction.getId(),
                account.getId(),
                card.getId(),
                savedTransaction.getAmount(),
                savedTransaction.getPaymentDescription(),
                savedTransaction.getTransactionDate());
    }

    public ResponseEntity<List<TransactionResponseDto>> getAllTransactionsByCpf(String cpf) {
        Optional<Account> optAccount = accountRepository.findByCpf(cpf);

        if (optAccount.isPresent()) {
            Account account = optAccount.get();

            List<Transaction> transactions = transactionRepository.findByAccountCpf(account.getCpf());

            if (transactions.isEmpty()) {
                return ResponseEntity.noContent().build();
            } else {
                List<TransactionResponseDto> responseDtos = transactions.stream()
                        .map(tx -> new TransactionResponseDto(
                                tx.getId(),
                                tx.getAccount().getId(),
                                tx.getCard().getId(),
                                tx.getAmount(),
                                tx.getPaymentDescription(),
                                tx.getTransactionDate()))
                        .collect(Collectors.toList());
                return ResponseEntity.ok(responseDtos);
            }
        } else {
            return ResponseEntity.noContent().build();
        }
    }
}
