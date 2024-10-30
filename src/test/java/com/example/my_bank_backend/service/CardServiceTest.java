package com.example.my_bank_backend.service;

import com.example.my_bank_backend.domain.account.Account;
import com.example.my_bank_backend.domain.card.Card;
import com.example.my_bank_backend.dto.TransactionResponseDto;
import com.example.my_bank_backend.exception.AccountNotFoundException;
import com.example.my_bank_backend.exception.CardAlreadyExistsException;
import com.example.my_bank_backend.exception.CardNotFoundException;
import com.example.my_bank_backend.repositories.AccountRepository;
import com.example.my_bank_backend.repositories.CardRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class CardServiceTest {

    @InjectMocks
    private CardService cardService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private InvoiceService invoiceService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private Card card;
    private Account account;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        account = new Account();
        account.setId(1L);
        account.setCpf("12345678909");
        account.setCreditLimit(2000.0);
        account.setUsedLimit(1000.0);

        card = new Card();
        card.setId(1L);
        card.setCardName("Meu Cartão");
        card.setCardNumber("1234 5678 9876 5432");
        card.setCardPassword("senha123");
        card.setCvv("123");
        card.setCardValue(1000.0);
        card.setExpirationDate("10/2030");
        card.setCardStatus("Active");
        card.setAccount(account);
    }

    @Test
    void testCreateCard() {
        when(accountRepository.findByCpf("12345678909")).thenReturn(Optional.of(account));
        when(cardRepository.findByCardNumberAndAccount(anyString(), any())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(cardRepository.save(any(Card.class))).thenReturn(card);

        Card createdCard = cardService.createCard("12345678909", "Meu Cartão", "senha123", 1000.0);

        assertNotNull(createdCard);
        assertEquals("Meu Cartão", createdCard.getCardName());
        verify(cardRepository, times(1)).save(any(Card.class));
    }

    @Test
    void testCreateCard_AccountNotFound() {
        when(accountRepository.findByCpf("12345678909")).thenReturn(Optional.empty());

        Exception exception = assertThrows(AccountNotFoundException.class, () -> {
            cardService.createCard("12345678909", "Meu Cartão", "senha123", 1000.0);
        });

        assertEquals("Account not found for CPF: 12345678909", exception.getMessage());
    }

    @Test
    void testCreateCard_CardAlreadyExists() {
        when(accountRepository.findByCpf("12345678909")).thenReturn(Optional.of(account));
        when(cardRepository.findByCardNumberAndAccount(anyString(), any())).thenReturn(Optional.of(card));

        Exception exception = assertThrows(CardAlreadyExistsException.class, () -> {
            cardService.createCard("12345678909", "Meu Cartão", "senha123", 1000.0);
        });

        assertEquals("Card already exists for this account.", exception.getMessage());
    }

    @Test
    void testGetCardByAccountCpf() {
        when(cardRepository.findCardsByAccountCpf("12345678909")).thenReturn(List.of(card));

        List<Card> cards = cardService.getCardByAccountCpf("12345678909");
        assertNotNull(cards);
        assertEquals(1, cards.size());
        assertEquals("Meu Cartão", cards.get(0).getCardName());
        verify(cardRepository, times(1)).findCardsByAccountCpf("12345678909");
    }

    @Test
    void testBuyWithCard() {
        Card testCard = new Card();
        testCard.setId(1L);
        testCard.setCardValue(1000.0);

        Account testAccount = new Account();
        testAccount.setId(1L);
        testAccount.setCreditLimit(2000.0);
        testAccount.setUsedLimit(1000.0);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(testCard));
        when(accountRepository.findByCpf("12345678909")).thenReturn(Optional.of(testAccount));
        when(invoiceService.createInvoice(any(), anyDouble(), anyInt(), anyInt())).thenReturn("SUCCESS");

        TransactionResponseDto savedTransaction = new TransactionResponseDto(1L, testAccount.getId(), testCard.getId(),
                200.0, "Purchase made!", null);
        when(transactionService.processTransaction(any(), any(), anyDouble(), any())).thenReturn(savedTransaction);

        String result = cardService.buyWithCard(1L, "12345678909", 200.0);

        assertEquals("Purchase successful! Transaction ID: 1", result);
        assertEquals(800.0, testCard.getCardValue());
        assertEquals(1200.0, testAccount.getUsedLimit());
    }

    @Test
    void testBuyWithCard_InsufficientLimit() {
        account.setUsedLimit(1900.0);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(accountRepository.findByCpf("12345678909")).thenReturn(Optional.of(account));

        String result = cardService.buyWithCard(1L, "12345678909", 200.0);
        assertEquals("Insufficient limit!", result);
    }

    @Test
    void testDisableCard_Success() {

        Card cardTestCard = new Card();
        cardTestCard.setIsActive(true);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        cardService.disableCard(1L);

        assertFalse(card.getIsActive());
        verify(cardRepository, times(1)).save(card);
    }

    @Test
    void testDisableCard_NotFound() {
        
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(CardNotFoundException.class, () -> {
            cardService.disableCard(1L);
        });

        assertEquals("Card not found", exception.getMessage());

        verify(cardRepository, never()).save(any(Card.class));
    }

}
