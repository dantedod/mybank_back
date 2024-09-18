package com.example.my_bank_backend.service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.my_bank_backend.domain.account.Account;
import com.example.my_bank_backend.domain.card.Card;
import com.example.my_bank_backend.domain.invoice.Invoice;
import com.example.my_bank_backend.dto.CardRequestDto;
import com.example.my_bank_backend.repositories.AccountRepository;
import com.example.my_bank_backend.repositories.CardRepository;
import com.example.my_bank_backend.repositories.InvoiceRepository;

@Service
public class CardService {

    private final SecureRandom secureRandom = new SecureRandom();
    private final Set<String> generatedCards = new HashSet<>();
    private final Set<String> generatedCvv = new HashSet<>();

    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;
    private final InvoiceRepository invoiceRepository;

    public CardService(AccountRepository accountRepository, CardRepository cardRepository,
            InvoiceRepository invoiceRepository) {
        this.accountRepository = accountRepository;
        this.cardRepository = cardRepository;
        this.invoiceRepository = invoiceRepository;
    }

    public ResponseEntity<CardRequestDto> createCard(String accountCpf, String cardName, String cardPassword,
            Double cardValue) {
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

    public ResponseEntity<List<Card>> getCardByAccountCpf(@PathVariable String accountCpf) {
        List<Card> cards = cardRepository.findCardsByAccountCpf(accountCpf);
        return ResponseEntity.ok(cards);
    }

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
                        return ResponseEntity.badRequest()
                                .body("You cannot create an invoice before the minimum allowed date!");
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

    public ResponseEntity<Void> deleteCard(@PathVariable Long cardId) {
        if (!cardRepository.existsById(cardId)) {
            return ResponseEntity.notFound().build();
        }

        cardRepository.deleteById(cardId);
        return ResponseEntity.noContent().build();
    }
}
