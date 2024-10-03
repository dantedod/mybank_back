package com.example.my_bank_backend.dto;

import java.time.LocalDateTime;

import com.example.my_bank_backend.domain.enums.TransferenceEnum;

public record TransferResponseDto(
        Long id,
        String cpfSender,
        String senderName,
        String cpfReceiver,
        String receiverName,
        Double amount,
        String paymentDescription,
        LocalDateTime transferenceDate,
        TransferenceEnum transactionType) {
}
