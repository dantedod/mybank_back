package com.example.my_bank_backend.controllers;

import java.util.List;

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

import com.example.my_bank_backend.domain.card.Card;
import com.example.my_bank_backend.dto.CardRequestDto;
import com.example.my_bank_backend.service.CardService;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/card")
@RequiredArgsConstructor
public class CardController {

  private final PasswordEncoder passwordEncoder;
  private final CardService cardService;

  @PostMapping("/create")
  public ResponseEntity<CardRequestDto> createCard(@RequestBody CardRequestDto cardRequestDto) {
    return cardService.createCard(cardRequestDto.accountCpf(), cardRequestDto.cardName(),
        passwordEncoder.encode(cardRequestDto.cardPassword()),
        cardRequestDto.cardValue());
  }

  @GetMapping("/{accountCpf}")
  public ResponseEntity<List<Card>> getCardByAccountCpf(@PathVariable String accountCpf) {
    return cardService.getCardByAccountCpf(accountCpf);
  }

  @PostMapping("/buy/{accountCpf}/{cardId}/{purchaseAmount}")
  public ResponseEntity<String> buyWithCard(@PathVariable Long cardId, @PathVariable String accountCpf,
      @PathVariable Double purchaseAmount) {
    return cardService.buyWithCard(cardId, accountCpf, purchaseAmount);
  }

  @DeleteMapping("/{cardId}")
  public ResponseEntity<Void> deleteCard(@PathVariable Long cardId) {
    return cardService.deleteCard(cardId);
  }
}
