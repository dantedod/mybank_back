package com.example.my_bank_backend.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

@CrossOrigin(origins = {"http://localhost:4200", "https://mybank-front-3d93fg8dq-mateus-quixadas-projects.vercel.app"})
@RestController
@RequestMapping("/card")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @PostMapping("/create")
    public ResponseEntity<CardRequestDto> createCard(@RequestBody CardRequestDto cardRequestDto) {

        try {
            Card card = cardService.createCard(cardRequestDto.accountCpf(), cardRequestDto.cardName(),
                    cardRequestDto.cardPassword(), cardRequestDto.cardValue());

            if (card != null) {
                return ResponseEntity.ok(new CardRequestDto(card.getAccount().getCpf(), card.getCardName(),
                        card.getCardNumber(), card.getCardPassword(), card.getCvv(), card.getCardValue(),
                        card.getExpirationDate(), card.getCardStatus()));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @GetMapping("/{accountCpf}")
    public ResponseEntity<List<Card>> getCardByAccountCpf(@PathVariable String accountCpf) {

        try {
            List<Card> cards = cardService.getCardByAccountCpf(accountCpf);

            return ResponseEntity.ok(cards);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/buy/{accountCpf}/{cardId}/{purchaseAmount}")
    public ResponseEntity<String> buyWithCard(@PathVariable Long cardId, @PathVariable String accountCpf,
            @PathVariable Double purchaseAmount) {

        try {
            String buy = cardService.buyWithCard(cardId, accountCpf, purchaseAmount);

            return ResponseEntity.ok(buy);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<String> deleteCard(@PathVariable Long cardId) {
        String deleteCardResponse = cardService.deleteCard(cardId);

        if (deleteCardResponse.equals("Card not found!")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(deleteCardResponse);
        }

        return ResponseEntity.ok(deleteCardResponse);
    }
}
