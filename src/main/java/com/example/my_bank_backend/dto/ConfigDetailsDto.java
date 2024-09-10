package com.example.my_bank_backend.dto;

public record ConfigDetailsDto(
      String cpf,
      String phone,
      String email,
      String password,
      String confirmPassword) {
}