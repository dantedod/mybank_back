package com.example.my_bank_backend.dto;

import java.time.LocalDateTime;

public record TransactionRequestDto(
        Long accountId,
        Long cardId,
        Double amount,
        String paymentDescription,
        LocalDateTime transactionDate) {
}
