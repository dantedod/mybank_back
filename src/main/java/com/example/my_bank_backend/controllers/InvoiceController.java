package com.example.my_bank_backend.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
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

  @GetMapping("{accountCpf}")
  public ResponseEntity<List<Invoice>> getAllInvoices(@PathVariable String accountCpf) {

    try {
      List<Invoice> invoices = invoiceService.getAllInvoices(accountCpf);

      if (invoices.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
      }

      return ResponseEntity.ok(invoices);
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @PostMapping("/addvalue/{invoiceId}/{value}")
  public ResponseEntity<String> addValue(@PathVariable Long invoiceId, @PathVariable Double value) {

    if (invoiceId == null || value == null || value <= 0) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid invoiceId or value");
    }

    try {
      String result = invoiceService.addValue(invoiceId, value);
      return ResponseEntity.ok(result);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while adding value");
    }
  }

  @GetMapping("/account/{accountCpf}")
  public ResponseEntity<Invoice> getInvoiceByAccount(@PathVariable String accountCpf) {

    if (accountCpf == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    try {
      Invoice invoice = invoiceService.getInvoiceByAccount(accountCpf);

      return ResponseEntity.ok(invoice);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @PostMapping("/pay")
  public ResponseEntity<Invoice> payInvoice(@RequestBody PaymentInvoiceDto paymentInvoiceDto) {

    if (paymentInvoiceDto == null){
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    try {
      Invoice invoice = invoiceService.payInvoice(paymentInvoiceDto.accountCpf(), paymentInvoiceDto.payValue());
      
      return ResponseEntity.ok(invoice);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}