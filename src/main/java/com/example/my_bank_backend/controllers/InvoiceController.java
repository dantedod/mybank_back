package com.example.my_bank_backend.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.my_bank_backend.domain.invoice.Invoice;
import com.example.my_bank_backend.dto.PaymentInvoiceDto;
import com.example.my_bank_backend.service.InvoiceService;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/invoice")
@RequiredArgsConstructor
public class InvoiceController {

  private final InvoiceService invoiceService;

  @GetMapping
  public ResponseEntity<List<Invoice>> getAllInvoices() {
    return invoiceService.getAllInvoices();
  }

  @PostMapping("/addvalue/{invoiceId}/{value}")
  public ResponseEntity<String> addValue(@PathVariable Long invoiceId, @PathVariable Double value) {
    return invoiceService.addValue(invoiceId, value);
  }

  @GetMapping("/account/{accountCpf}")
  public ResponseEntity<Optional<Invoice>> getInvoiceByAccount(@PathVariable String accountCpf) {
    return invoiceService.getInvoiceByAccount(accountCpf);
  }

  @PostMapping("/pay")
  public ResponseEntity<String> payInvoice(@RequestBody PaymentInvoiceDto paymentInvoiceDto) {
    return invoiceService.payInvoice(paymentInvoiceDto.accountCpf(), paymentInvoiceDto.payValue()); 
  }
}