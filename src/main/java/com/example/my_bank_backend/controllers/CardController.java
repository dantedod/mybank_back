package com.example.my_bank_backend.controllers;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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

    private AccountRepository accountRepository;

    private CardService cardService;

    @PostMapping("/create")
    public ResponseEntity<String> createCard(@RequestBody CardRequestDto body) {

        Optional<Account> optAccount = accountRepository.findById(body.accountId());

        if (optAccount.isPresent()) {
            Account account = optAccount.get();

            Optional<Card> existingCard = cardRepository.findByCardNumberAndAccount(body.cardNumber(), account);
            if (existingCard.isPresent()) {
                return ResponseEntity.badRequest().body("Já existe um cartão associada a essa conta!");
            }

            Card card = new Card();
            card.setCardName(body.cardName());
            card.setCardNumber(cardService.generateCardNumber());
            card.setCardPassword(body.cardPassword());
            card.setCvv(Integer.parseInt(cardService.generateCvv()));
            card.setCardValue(body.cardValue());
            card.setExpirationDate("10/2030");
            card.setCardStatus("Ativo");

            card.setAccount(account);
            cardRepository.save(card);
            return ResponseEntity.ok("Cartão criado com sucesso!");
        } else {
            return ResponseEntity.badRequest().body("Fudeu, conta não encontrada!");
        }
    }
}
