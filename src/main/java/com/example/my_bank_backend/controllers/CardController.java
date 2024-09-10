package com.example.my_bank_backend.controllers;

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
import com.example.my_bank_backend.dto.CardRequestDto;
import com.example.my_bank_backend.repositories.AccountRepository;
import com.example.my_bank_backend.repositories.CardRepository;
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

  @Autowired
  public CardController(CardRepository cardRepository, AccountRepository accountRepository, CardService cardService, PasswordEncoder passwordEncoder) {
    this.cardRepository = cardRepository;
    this.accountRepository = accountRepository;
    this.cardService = cardService;
    this.passwordEncoder = passwordEncoder;
  }

  @PostMapping("/create")
  public ResponseEntity<CardRequestDto> createCard(@RequestBody CardRequestDto cardRequestDto) {
    return cardService.createCard(cardRequestDto.accountCpf(), cardRequestDto.cardName(), passwordEncoder.encode(cardRequestDto.cardPassword()),
        cardRequestDto.cardValue());
  }

  @GetMapping("/{accountCpf}")
  public ResponseEntity<List<Card>> getCardByAccountCpf(@PathVariable String accountCpf) {
    List<Card> cards = cardRepository.findCardsByAccountCpf(accountCpf);
    return ResponseEntity.ok(cards);
  }

  @PostMapping("/buy/{cardId}/{purchaseAmount}")
  public ResponseEntity<String> buyWithCard(@PathVariable Long cardId, @PathVariable Double purchaseAmount) {
    Optional<Card> optCard = cardRepository.findById(cardId);

    if (optCard.isPresent()) {
      Card card = optCard.get();
      Account account = card.getAccount();

      if (account.getCreditLimit() - account.getUsedLimit() >= purchaseAmount) {
        account.setUsedLimit(account.getUsedLimit() + purchaseAmount);

        card.setCardValue(card.getCardValue() - purchaseAmount);

        cardRepository.save(card);
        accountRepository.save(account);

        return ResponseEntity.ok("Ok!");
      } else {
        return ResponseEntity.badRequest().body("deu ruim!");
      }
    } else {
      return ResponseEntity.notFound().build();
    }
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
