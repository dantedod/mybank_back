package com.example.my_bank_backend.service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.my_bank_backend.domain.account.Account;
import com.example.my_bank_backend.domain.card.Card;
import com.example.my_bank_backend.dto.CardRequestDto;
import com.example.my_bank_backend.repositories.AccountRepository;
import com.example.my_bank_backend.repositories.CardRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class CardService {

    private final SecureRandom secureRandom = new SecureRandom();
    private final Set<String> generatedCards = new HashSet<>();
    private final Set<String> generatedCvv = new HashSet<>();

    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;
    private final InvoiceService invoiceService;
    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<CardRequestDto> createCard(String accountCpf, String cardName, String cardPassword,
            Double cardValue) {
        Optional<Account> optAccount = accountRepository.findByCpf(accountCpf);

        if (optAccount.isPresent()) {
            Account account = optAccount.get();

            String cardNumber = generateCardNumber();

            Optional<Card> existingCard = cardRepository.findByCardNumberAndAccount(cardNumber, account);
            if (existingCard.isPresent()) {
                return ResponseEntity.badRequest().body(null);
            }

            String cvv = generateCvv();
            Card card = new Card();
            card.setCardName(cardName);
            card.setCardNumber(cardNumber);
            card.setCardPassword(passwordEncoder.encode(cardPassword));
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
                    "Ativo");

            return ResponseEntity.ok(cardDto);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    private String generateCardNumber() {
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

    private String generateCvv() {
        String cvv;
        do {
            cvv = String.format("%03d", secureRandom.nextInt(1000));
        } while (generatedCvv.contains(cvv));

        generatedCvv.add(cvv);
        return cvv;
    }

    public ResponseEntity<List<Card>> getCardByAccountCpf(String accountCpf) {
        List<Card> cards = cardRepository.findCardsByAccountCpf(accountCpf);
        return ResponseEntity.ok(cards);
    }

    public ResponseEntity<String> buyWithCard(Long cardId, String accountCpf, Double purchaseAmount) {
        Optional<Card> optCard = cardRepository.findById(cardId);
        Optional<Account> optAccount = accountRepository.findByCpf(accountCpf);

        if (!optAccount.isPresent()) {
            return ResponseEntity.badRequest().body("Account not found!");
        }

        Account account = optAccount.get();

        if (optCard.isPresent()) {
            Card card = optCard.get();

            if (account.getCreditLimit() - account.getUsedLimit() >= purchaseAmount) {
                account.setUsedLimit(account.getUsedLimit() + purchaseAmount);
                card.setCardValue(card.getCardValue() - purchaseAmount);

                cardRepository.save(card);
                accountRepository.save(account);

                return invoiceService.createInvoice(account, purchaseAmount, LocalDate.now().getMonthValue(),
                        LocalDate.now().getYear());
            }
            return ResponseEntity.badRequest().body("Insufficient limit!");
        }
        return ResponseEntity.notFound().build();
    }

    public ResponseEntity<Void> deleteCard(Long cardId) {
        if (!cardRepository.existsById(cardId)) {
            return ResponseEntity.notFound().build();
        }

        cardRepository.deleteById(cardId);
        return ResponseEntity.noContent().build();
    }
}
