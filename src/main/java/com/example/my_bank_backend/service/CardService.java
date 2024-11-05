package com.example.my_bank_backend.service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.my_bank_backend.domain.account.Account;
import com.example.my_bank_backend.domain.card.Card;
import com.example.my_bank_backend.dto.TransactionResponseDto;
import com.example.my_bank_backend.exception.AccountNotFoundException;
import com.example.my_bank_backend.exception.CardAlreadyExistsException;
import com.example.my_bank_backend.exception.CardNotFoundException;
import com.example.my_bank_backend.exception.ExceedAccountLimitException;
import com.example.my_bank_backend.exception.ExceedActualAccountLimitException;
import com.example.my_bank_backend.exception.InsufficientCardValueException;
import com.example.my_bank_backend.exception.InsufficientLimitException;
import com.example.my_bank_backend.repositories.AccountRepository;
import com.example.my_bank_backend.repositories.CardRepository;

import jakarta.transaction.Transactional;
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
  private final TransactionService transactionService;

  public Card createCard(String accountCpf, String cardName, String cardPassword, Double cardValue) {
    validateCardInput(accountCpf, cardName, cardValue);

    Account account = accountRepository.findByCpf(accountCpf)
        .orElseThrow(() -> new AccountNotFoundException("Account not found for CPF: " + accountCpf));

    Double avaibleLimit = account.getCreditLimit() - account.getUsedLimit();
    if (cardValue > account.getCreditLimit()) {
      throw new ExceedAccountLimitException("Card value cannot exceed account credit limit." + avaibleLimit);
    }

    if (cardValue > avaibleLimit) {
      throw new ExceedActualAccountLimitException("Card value cannot exceed actual account credit limit.");
    }

    String cardNumber = generateCardNumber();
    if (cardRepository.findByCardNumberAndAccount(cardNumber, account).isPresent()) {
      throw new CardAlreadyExistsException("Card already exists for this account.");
    }

    Boolean isActive = true;
    if (cardRepository.findByCardNameAndAccountAndIsActive(cardName, account, isActive).isPresent()) {
      throw new CardAlreadyExistsException("An active card with the same name already exists for this account.");
    }

    String cvv = generateCvv();
    Card newCard = new Card();
    newCard.setCardName(cardName);
    newCard.setCardNumber(cardNumber);
    newCard.setCardPassword(passwordEncoder.encode(cardPassword));
    newCard.setCvv(cvv);
    newCard.setCardValue(cardValue);
    newCard.setExpirationDate("10/2030");
    newCard.setCardStatus("Active");
    newCard.setAccount(account);
    newCard.setIsActive(true);

    return cardRepository.save(newCard);
  }

  private void validateCardInput(String accountCpf, String cardName, Double cardValue) {
    if (accountCpf == null || accountCpf.isEmpty()) {
      throw new IllegalArgumentException("Account CPF cannot be null or empty.");
    }
    if (cardName == null || cardName.isEmpty()) {
      throw new IllegalArgumentException("Card name cannot be null or empty.");
    }
    if (cardValue == null || cardValue <= 0) {
      throw new IllegalArgumentException("Card value must be greater than zero.");
    }
  }

  String generateCardNumber() {
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

  public List<Card> getCardByAccountCpf(String accountCpf) {
    return cardRepository.findCardsByAccountCpf(accountCpf);
  }

  @Transactional
  public String buyWithCard(Long cardId, String accountCpf, Double purchaseAmount) {
    Card card = cardRepository.findById(cardId)
        .orElseThrow(() -> new IllegalArgumentException("Card not found!"));

    Account account = accountRepository.findByCpf(accountCpf)
        .orElseThrow(() -> new IllegalArgumentException("Account not found!"));

    if (account.getCreditLimit() - account.getUsedLimit() < purchaseAmount) {
      throw new InsufficientLimitException("Insufficient Limit!");
    }

    if (purchaseAmount > card.getCardValue()) {
      throw new InsufficientCardValueException("Your card does not have enough value for this purchase!");
    }

    account.setUsedLimit(account.getUsedLimit() + purchaseAmount);
    card.setCardValue(card.getCardValue() - purchaseAmount);

    cardRepository.save(card);
    accountRepository.save(account);

    String invoiceResponse = invoiceService.createInvoice(account, purchaseAmount,
        LocalDate.now().getMonthValue(), LocalDate.now().getYear());

    if (invoiceResponse == null || (!invoiceResponse.equalsIgnoreCase("Invoice updated") &&
        !invoiceResponse.equalsIgnoreCase("SUCCESS"))) {
      return "Invoice creation failed: " + (invoiceResponse != null ? invoiceResponse : "No response received");
    }

    TransactionResponseDto transactionResponse = transactionService.processTransaction(
        account.getId(), card.getId(), purchaseAmount, "Purchase made!");

    if (transactionResponse == null) {
      throw new IllegalStateException("Transaction processing failed!");
    }

    return "Purchase successful! Transaction ID: " + transactionResponse.id();
  }

  public void disableCard(Long id) {
    Card card = cardRepository.findById(id).orElseThrow(() -> new CardNotFoundException("Card not found"));

    card.setIsActive(false);
    cardRepository.save(card);
  }

}
