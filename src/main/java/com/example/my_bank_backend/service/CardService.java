package com.example.my_bank_backend.service;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.my_bank_backend.domain.account.Account;
import com.example.my_bank_backend.domain.card.Card;
import com.example.my_bank_backend.dto.CardRequestDto;
import com.example.my_bank_backend.repositories.AccountRepository;
import com.example.my_bank_backend.repositories.CardRepository;

@Service
public class CardService {

    private final SecureRandom secureRandom = new SecureRandom();
    private final Set<String> generatedCards = new HashSet<>();
    private final Set<String> generatedCvv = new HashSet<>();

    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;

    @Autowired
    public CardService(AccountRepository accountRepository, CardRepository cardRepository) {
        this.accountRepository = accountRepository;
        this.cardRepository = cardRepository;
    }

    public ResponseEntity<CardRequestDto> createCard(String accountCpf, String cardName, String cardPassword, Double cardValue) {
        Optional<Account> optAccount = accountRepository.findByCpf(accountCpf);

        if (optAccount.isPresent()) {
            Account account = optAccount.get();

            Optional<Card> existingCard = cardRepository.findByCardNumberAndAccount(generateCardNumber(), account);
            if (existingCard.isPresent()) {
                return ResponseEntity.badRequest().body(null);
            }

            String cardNumber = generateCardNumber();
            Integer cvv = Integer.parseInt(generateCvv());
            Card card = new Card();
            card.setCardName(cardName);
            card.setCardNumber(cardNumber);
            card.setCardPassword(cardPassword);
            card.setCvv(cvv);
            card.setCardValue(cardValue);
            card.setExpirationDate("10/2030");
            card.setCardStatus("Ativo");
            card.setAccount(account);

            cardRepository.save(card);

            CardRequestDto cardDto = new CardRequestDto(
                    accountCpf,
                    cardName,
                    cardNumber,
                    cardPassword,
                    cvv,
                    cardValue,
                    "10/2030",
                    "Ativo"
            );

            return ResponseEntity.ok(cardDto);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    public String generateCardNumber() {
        String cardNumber;
        do {
            cardNumber = String.format("%04d %04d %04d %04d",
                    secureRandom.nextInt(10000),
                    secureRandom.nextInt(10000),
                    secureRandom.nextInt(10000),
                    secureRandom.nextInt(10000));
        } while (generatedCards.contains(cardNumber));

        generatedCards.add(cardNumber);
        return cardNumber;
    }

    public String generateCvv() {
        String cvv;
        do {
            cvv = String.format("%03d", secureRandom.nextInt(1000));
        } while (generatedCvv.contains(cvv));

        generatedCvv.add(cvv);
        return cvv;
    }
}
