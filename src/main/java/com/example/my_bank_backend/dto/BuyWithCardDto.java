package com.example.my_bank_backend.dto;

public record BuyWithCardDto(
    Long cardId,
    Double purchaseAmount,
    String cardPassword,
    String accountCpf,
    String paymentDescription
) {
}