package com.example.my_bank_backend.dto;

import com.example.my_bank_backend.domain.enums.TransactionEnum;

public record TransactionRequestDto(
        String cpfSender,
        String cpfReceiver,
        Double amount,
        String paymentDescription,
        TransactionEnum transactionType) {
}
