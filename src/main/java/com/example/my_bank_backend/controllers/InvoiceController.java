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
import com.example.my_bank_backend.domain.card.Card;
import com.example.my_bank_backend.domain.invoice.Invoice;
import com.example.my_bank_backend.dto.InvoiceRequestDto;
import com.example.my_bank_backend.repositories.AccountRepository;
import com.example.my_bank_backend.repositories.CardRepository;
import com.example.my_bank_backend.repositories.InvoiceRepository;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/invoice")
public class InvoiceController {

  Logger logger = Logger.getLogger(InvoiceController.class.getName());

  private CardRepository cardRepository;
  private InvoiceRepository invoiceRepository;
  private AccountRepository accountRepository;

  @Autowired
  public InvoiceController(CardRepository cardRepository, InvoiceRepository invoiceRepository,
      AccountRepository accountRepository) {
    this.cardRepository = cardRepository;
    this.invoiceRepository = invoiceRepository;
    this.accountRepository = accountRepository;
  }

  @GetMapping
  public ResponseEntity<List<Invoice>> getAllInvoices() {
    return ResponseEntity.ok(invoiceRepository.findAll());
  }

  @PostMapping("/{cardId}/create")
public ResponseEntity<String> createInvoice(@PathVariable Long cardId, @RequestBody InvoiceRequestDto invoiceDto) {
    Optional<Card> optCard = cardRepository.findById(cardId);

    if (!optCard.isPresent()) {
        return ResponseEntity.badRequest().body("Card not found!");
    }

    Card card = optCard.get();

    // Obtenha o mês e o ano da fatura a ser criada
    LocalDate invoiceDate = LocalDate.now(); // ou use a data fornecida por invoiceDto, se houver
    int invoiceMonth = invoiceDate.getMonthValue();
    int invoiceYear = invoiceDate.getYear();

    // Verifique se já existe uma fatura para o cartão no mesmo mês e ano
    boolean existsInvoice = invoiceRepository.existsByCardAndMonthAndYear(card, invoiceMonth, invoiceYear);
    if (existsInvoice) {
        return ResponseEntity.badRequest().body("An invoice already exists for this card for the given month and year");
    }

    LocalDate minAllowedDate = LocalDate.of(2024, 1, 1);
    LocalDate currentDate = LocalDate.now();

    if (currentDate.isBefore(minAllowedDate)) {
        return ResponseEntity.badRequest().body("You cannot create an invoice before the minimum allowed date!");
    }

    Invoice newInvoice = new Invoice();
    newInvoice.setCard(card);
    newInvoice.setInvoiceDescription(invoiceDto.invoiceDescription());
    newInvoice.setCardName(card.getCardName());
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
      Optional<Card> optCard = cardRepository.findCardByAccountCpf(accountCpf);

      if (optCard.isPresent()) {
        Long cardId = optCard.get().getId();

        LocalDate currentDate = LocalDate.now();
        int currentMonth = currentDate.getMonthValue();
        int currentYear = currentDate.getYear();

        List<Invoice> invoices = invoiceRepository.findByDateMonthAndYear(currentMonth, currentYear);

        Optional<Invoice> optInvoice = invoices.stream()
            .filter(invoice -> invoice.getCard().getId().equals(cardId))
            .findFirst();

        if (optInvoice.isPresent()) {
          return ResponseEntity.ok(optInvoice);
        }
        return ResponseEntity.notFound().build();
      }
      return ResponseEntity.badRequest().body(null);
    }
    return ResponseEntity.notFound().build();
  }
}