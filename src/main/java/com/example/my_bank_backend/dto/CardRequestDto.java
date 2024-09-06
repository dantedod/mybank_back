package com.example.my_bank_backend.dto;

public record CardRequestDto(
    String accountCpf,  
    String cardName,
    String cardNumber,
    String cardPassword,
    Integer cvv,
    Double cardValue,
    String expirationDate,
    String cardStatus) {
}
