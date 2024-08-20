package com.example.my_bank_backend.controllers;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
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

@RestController
@RequestMapping("/invoice")
public class InvoiceController {

  private CardRepository cardRepository;

  private InvoiceRepository invoiceRepository;

  @GetMapping
  public ResponseEntity<List<Invoice>> getAllInvoices() {
    return ResponseEntity.ok(invoiceRepository.findAll());
  }

  @PostMapping("/{cardId}/create")
  public ResponseEntity<String> createInvoice(@PathVariable Long cardId, @RequestBody InvoiceRequestDto invoiceDto) {
    // Busca o cartão pelo ID
    Optional<Card> optCard = cardRepository.findById(cardId);

    if (optCard.isPresent()) {
      Card card = optCard.get();

      Invoice newInvoice = new Invoice();
      // Preenche os campos da fatura com as informações necessárias
      newInvoice.setCard(card);
      newInvoice.setCardName(card.getCardName()); // Supondo que card tenha um campo cardName

      newInvoice.setInvoiceDescription(invoiceDto.invoiceDescription());

      newInvoice.setAmount(invoiceDto.amount());

      newInvoice.setEmail(invoiceDto.email());

      // Converter LocalDate para Date
      Date invoiceDate = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
      newInvoice.setInvoiceDate(invoiceDate); // Define a data da fatura como a data atual

      // Definir data de vencimento em 30 dias
      Date dueDate = Date.from(LocalDate.now().plusDays(30).atStartOfDay(ZoneId.systemDefault()).toInstant());
      newInvoice.setDueDate(dueDate);

      newInvoice.setInvoiceStatus(invoiceDto.invoiceStatus()); // Status inicial da fatura

      // Salva a fatura
      newInvoice.setCard(card);
      invoiceRepository.save(newInvoice);
      return ResponseEntity.ok("Fatura criada com sucesso!");
    } else {
      return ResponseEntity.badRequest().body("Cartão não encontrado!");
    }
  }
}