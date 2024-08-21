package com.example.my_bank_backend.controllers;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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

    if (optCard.isPresent()) {
      Card card = optCard.get();

      Invoice newInvoice = new Invoice();
      newInvoice.setCard(card);

      newInvoice.setInvoiceDescription(invoiceDto.invoiceDescription());

      newInvoice.setAmount(invoiceDto.amount());

      newInvoice.setEmail(invoiceDto.email());

      Date invoiceDate = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
      newInvoice.setInvoiceDate(invoiceDate);

      Date dueDate = Date.from(LocalDate.now().plusDays(30).atStartOfDay(ZoneId.systemDefault()).toInstant());
      newInvoice.setDueDate(dueDate);

      newInvoice.setInvoiceStatus(invoiceDto.invoiceStatus());

      newInvoice.setCard(card);
      invoiceRepository.save(newInvoice);
      return ResponseEntity.ok("Fatura criada com sucesso!");
    } else {
      return ResponseEntity.badRequest().body("Cartão não encontrado!");
    }
  }
}