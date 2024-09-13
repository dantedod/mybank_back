package com.example.my_bank_backend.controllers;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.my_bank_backend.domain.account.Account;
import com.example.my_bank_backend.domain.card.Card;
import com.example.my_bank_backend.domain.invoice.Invoice;
import com.example.my_bank_backend.dto.CardRequestDto;
import com.example.my_bank_backend.repositories.AccountRepository;
import com.example.my_bank_backend.repositories.CardRepository;
import com.example.my_bank_backend.repositories.InvoiceRepository;
import com.example.my_bank_backend.service.CardService;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/card")
@RequiredArgsConstructor
public class CardController {

  private CardRepository cardRepository;
  private final PasswordEncoder passwordEncoder;
  private AccountRepository accountRepository;
  private CardService cardService;
  private InvoiceRepository invoiceRepository;

  @Autowired
  public CardController(CardRepository cardRepository, AccountRepository accountRepository, CardService cardService,
      PasswordEncoder passwordEncoder, InvoiceRepository invoiceRepository) {
    this.cardRepository = cardRepository;
    this.accountRepository = accountRepository;
    this.cardService = cardService;
    this.passwordEncoder = passwordEncoder;
    this.invoiceRepository = invoiceRepository;
  }

  @PostMapping("/create")
  public ResponseEntity<CardRequestDto> createCard(@RequestBody CardRequestDto cardRequestDto) {
    return cardService.createCard(cardRequestDto.accountCpf(), cardRequestDto.cardName(),
        passwordEncoder.encode(cardRequestDto.cardPassword()),
        cardRequestDto.cardValue());
  }

  @GetMapping("/{accountCpf}")
  public ResponseEntity<List<Card>> getCardByAccountCpf(@PathVariable String accountCpf) {
    List<Card> cards = cardRepository.findCardsByAccountCpf(accountCpf);
    return ResponseEntity.ok(cards);
  }

  @PostMapping("/buy/{accountCpf}/{cardId}/{purchaseAmount}")
  public ResponseEntity<String> buyWithCard(@PathVariable Long cardId, @PathVariable String accountCpf,
      @PathVariable Double purchaseAmount) {
    Optional<Card> optCard = cardRepository.findById(cardId);
    Optional<Account> optAccount = accountRepository.findByCpf(accountCpf);

    if (!optAccount.isPresent()) {
      return ResponseEntity.badRequest().body("Account not found!");
    }

    Account account = optAccount.get();
    LocalDate invoiceDate = LocalDate.now();
    int invoiceMonth = invoiceDate.getMonthValue();
    int invoiceYear = invoiceDate.getYear();

    Optional<Invoice> existsInvoice = invoiceRepository.findByAccountAndMonthAndYear(account, invoiceMonth,
        invoiceYear);

    if (optCard.isPresent()) {
      Card card = optCard.get();

      if (account.getCreditLimit() - account.getUsedLimit() >= purchaseAmount) {
        account.setUsedLimit(account.getUsedLimit() + purchaseAmount);
        card.setCardValue(card.getCardValue() - purchaseAmount);

        cardRepository.save(card);
        accountRepository.save(account);

        if (existsInvoice.isEmpty()) {
          LocalDate minAllowedDate = LocalDate.of(2024, 1, 1);
          LocalDate currentDate = LocalDate.now();

          if (currentDate.isBefore(minAllowedDate)) {
            return ResponseEntity.badRequest().body("You cannot create an invoice before the minimum allowed date!");
          }

          Invoice newInvoice = new Invoice();
          newInvoice.setAccount(account);
          newInvoice.setAmount(purchaseAmount);
          newInvoice.setEmail(account.getUser().getEmail());
          Date invoiceStartDate = Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
          newInvoice.setInvoiceDate(invoiceStartDate);
          Date dueDate = Date.from(currentDate.plusDays(30).atStartOfDay(ZoneId.systemDefault()).toInstant());
          newInvoice.setDueDate(dueDate);
          newInvoice.setInvoiceStatus("Não paga!");
          newInvoice.setInvoiceDescription("Fatura do mês: " + invoiceMonth);

          invoiceRepository.save(newInvoice);
          return ResponseEntity.ok("Invoice created");
        } else {
          Invoice existingInvoice = existsInvoice.get();
          existingInvoice.setAmount(existingInvoice.getAmount() + purchaseAmount);
          invoiceRepository.save(existingInvoice);
          return ResponseEntity.ok("Invoice updated");
        }
      }
      return ResponseEntity.badRequest().body("Insufficient limit!");
    }
    return ResponseEntity.notFound().build();
  }

  @DeleteMapping("/{cardId}")
  public ResponseEntity<Void> deleteCard(@PathVariable Long cardId) {
    if (!cardRepository.existsById(cardId)) {
      return ResponseEntity.notFound().build();
    }

    cardRepository.deleteById(cardId);
    return ResponseEntity.noContent().build();
  }
}
