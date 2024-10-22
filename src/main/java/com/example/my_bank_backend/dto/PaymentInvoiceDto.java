package com.example.my_bank_backend.dto;

public record PaymentInvoiceDto(
  String accountCpf,
  Double payValue ){
}