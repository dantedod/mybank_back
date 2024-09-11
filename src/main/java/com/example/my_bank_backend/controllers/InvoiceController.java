package com.example.my_bank_backend.controllers;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.my_bank_backend.domain.account.Account;
import com.example.my_bank_backend.domain.invoice.Invoice;
import com.example.my_bank_backend.dto.InvoiceRequestDto;
import com.example.my_bank_backend.repositories.AccountRepository;
import com.example.my_bank_backend.repositories.InvoiceRepository;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/invoice")
public class InvoiceController {

  Logger logger = Logger.getLogger(InvoiceController.class.getName());

  private InvoiceRepository invoiceRepository;
  private AccountRepository accountRepository;

  @Autowired
  public InvoiceController(InvoiceRepository invoiceRepository,
      AccountRepository accountRepository) {
    this.invoiceRepository = invoiceRepository;
    this.accountRepository = accountRepository;
  }

  @GetMapping
  public ResponseEntity<List<Invoice>> getAllInvoices() {
    return ResponseEntity.ok(invoiceRepository.findAll());
  }

  @PostMapping("/{accountId}/create")
  public ResponseEntity<String> createInvoice(@PathVariable Long accountId, @RequestBody InvoiceRequestDto invoiceDto) {
    Optional<Account> optAccount = accountRepository.findById(accountId);

    if (!optAccount.isPresent()) {
      return ResponseEntity.badRequest().body("Account not found!");
    }

    Account account = optAccount.get();

    LocalDate invoiceDate = LocalDate.now();
    int invoiceMonth = invoiceDate.getMonthValue();
    int invoiceYear = invoiceDate.getYear();

    boolean existsInvoice = invoiceRepository.existsByAccountdAndMonthAndYear(account, invoiceMonth, invoiceYear);
    if (existsInvoice) {
      return ResponseEntity.badRequest().body("An invoice already exists for this card for the given month and year");
    }

    LocalDate minAllowedDate = LocalDate.of(2024, 1, 1);
    LocalDate currentDate = LocalDate.now();

    if (currentDate.isBefore(minAllowedDate)) {
      return ResponseEntity.badRequest().body("You cannot create an invoice before the minimum allowed date!");
    }

    Invoice newInvoice = new Invoice();
    newInvoice.setAccount(account);
    newInvoice.setInvoiceDescription(invoiceDto.invoiceDescription());
    newInvoice.setAmount(invoiceDto.amount());
    newInvoice.setEmail(invoiceDto.email());

    Date invoiceStartDate = Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    newInvoice.setInvoiceDate(invoiceStartDate);

    Date dueDate = Date.from(currentDate.plusDays(30).atStartOfDay(ZoneId.systemDefault()).toInstant());
    newInvoice.setDueDate(dueDate);

    newInvoice.setInvoiceStatus(invoiceDto.invoiceStatus());
    invoiceRepository.save(newInvoice);
    return ResponseEntity.ok("Successful invoice creation");
  }

  @PostMapping("/addvalue/{invoiceId}/{value}")
  public ResponseEntity<String> addValue(@PathVariable Long invoiceId, @PathVariable Double value) {

    Optional<Invoice> optInvoice = invoiceRepository.findById(invoiceId);

    if (optInvoice.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    Invoice existingInvoice = optInvoice.get();
    Date invoiceDate = existingInvoice.getInvoiceDate();

    LocalDate invoiceLocalDate = new java.sql.Date(invoiceDate.getTime()).toLocalDate();
    int invoiceMonth = invoiceLocalDate.getMonthValue();
    int invoiceYear = invoiceLocalDate.getYear();

    List<Invoice> invoices = invoiceRepository.findByDateMonthAndYear(invoiceMonth, invoiceYear);

    Optional<Invoice> matchingInvoice = invoices.stream()
        .filter(invoice -> invoice.getId().equals(invoiceId))
        .findFirst();

    if (matchingInvoice.isEmpty()) {
      return ResponseEntity.badRequest().body("Fatura não corresponde ao mês e ano fornecidos.");
    }

    Double existingAmount = existingInvoice.getAmount();
    Double newAmount = existingAmount + value;

    existingInvoice.setAmount(newAmount);
    invoiceRepository.save(existingInvoice);

    return ResponseEntity.ok("Valor adicionado à fatura existente!");
  }

  @GetMapping("/{invoiceId}")
  public ResponseEntity<Invoice> getInvoiceById(@PathVariable Long invoiceId) {

    Optional<Invoice> invoice = this.invoiceRepository.findById(invoiceId);

    if (invoice.isPresent()) {
      return ResponseEntity.ok(invoice.get());
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/account/{accountCpf}")
  public ResponseEntity<Optional<Invoice>> getInvoiceByAccount(@PathVariable String accountCpf) {

    Optional<Account> optAccount = accountRepository.findByCpf(accountCpf);

    if (optAccount.isPresent()) {
      long accountId = optAccount.get().getId();
      LocalDate currentDate = LocalDate.now();
      int currentMonth = currentDate.getMonthValue();
      int currentYear = currentDate.getYear();

      List<Invoice> invoices = invoiceRepository.findByDateMonthAndYear(currentMonth, currentYear);

      Optional<Invoice> optInvoice = invoices.stream()
          .filter(invoice -> invoice.getAccount().getId().equals(accountId))
          .findFirst();

      if (optInvoice.isPresent()) {
        return ResponseEntity.ok(optInvoice);
      }
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.notFound().build();
  }

  @GetMapping("/card/{accountId}")
  public ResponseEntity<List<Invoice>> getInvoicesByCardId(@PathVariable Long accountId) {
    List<Invoice> invoices = invoiceRepository.findInvoicesByAccountId(accountId);
    return ResponseEntity.ok(invoices);
  }
}