package com.example.my_bank_backend.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.my_bank_backend.domain.invoice.Invoice;
import com.example.my_bank_backend.service.InvoiceService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/invoice")
public class InvoiceController {

  private final InvoiceService invoiceService;

  public InvoiceController(InvoiceService invoiceService) {
    this.invoiceService = invoiceService;
  }

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

  @PostMapping("/pay/{accountCpf}")
  public ResponseEntity<String> payInvoice(@PathVariable String accountCpf) {
    return invoiceService.payInvoice(accountCpf);
  }
}