package com.example.my_bank_backend.controllers;

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

    @Autowired
    public CardController(CardRepository cardRepository, AccountRepository accountRepository, CardService cardService) {
        this.cardRepository = cardRepository;
        this.accountRepository = accountRepository;
        this.cardService = cardService;
    }

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

    @GetMapping("/{cardId}")
    public ResponseEntity<Card> getCardById(@PathVariable Long cardId) {
        Optional<Card> optCard = cardRepository.findById(cardId);

        return optCard.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/buy/{cardId}")
    public ResponseEntity<String> buyWithCard(@PathVariable Long cardId, @RequestBody Double purchaseAmount) {
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
}
