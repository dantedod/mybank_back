package com.example.my_bank_backend.dto;

import com.example.my_bank_backend.domain.enums.TransferenceEnum;

public record TransferRequestDto(
        String cpfSender,
        String cpfReceiver,
        Double amount,
        String paymentDescription,
        TransferenceEnum transferenceType) {
}
