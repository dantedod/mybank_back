package com.example.my_bank_backend.dto;

public record BuyWithCardNameDto(
    String cardName,
    Double purchaseAmount,
    String cardPassword,
    String accountCpf,
    String paymentDescription) {
  
}
