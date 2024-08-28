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

import com.example.my_bank_backend.domain.card.Card;
import com.example.my_bank_backend.domain.invoice.Invoice;
import com.example.my_bank_backend.dto.InvoiceRequestDto;
import com.example.my_bank_backend.repositories.CardRepository;
import com.example.my_bank_backend.repositories.InvoiceRepository;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/invoice")
public class InvoiceController {

  Logger logger = Logger.getLogger(InvoiceController.class.getName());

  private CardRepository cardRepository;
  private InvoiceRepository invoiceRepository;

  @Autowired
  public InvoiceController(CardRepository cardRepository, InvoiceRepository invoiceRepository) {
    this.cardRepository = cardRepository;
    this.invoiceRepository = invoiceRepository;
  }

  @GetMapping
  public ResponseEntity<List<Invoice>> getAllInvoices() {
    return ResponseEntity.ok(invoiceRepository.findAll());
  }

  @PostMapping("/{cardId}/create")
  public ResponseEntity<String> createInvoice(@PathVariable Long cardId, @RequestBody InvoiceRequestDto invoiceDto) {
    Optional<Card> optCard = cardRepository.findById(cardId);

    if (!optCard.isPresent()) {
      return ResponseEntity.ok("Card not found!");
    }

    Card card = optCard.get();

    boolean existsInvoice = invoiceRepository.existsByCard(card);
    if (existsInvoice) {
      return ResponseEntity.badRequest().body("An invoice already exists for this card");
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

    Date invoiceDate = Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    newInvoice.setInvoiceDate(invoiceDate);

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

  @GetMapping("/card/{cardId}")
  public ResponseEntity<List<Invoice>> getInvoicesByCardId(@PathVariable Long cardId) {
    List<Invoice> invoices = invoiceRepository.findInvoicesByCardId(cardId);
    return ResponseEntity.ok(invoices);
  }
} 