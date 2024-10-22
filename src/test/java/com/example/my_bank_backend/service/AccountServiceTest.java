package com.example.my_bank_backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.my_bank_backend.domain.account.Account;
import com.example.my_bank_backend.domain.user.User;
import com.example.my_bank_backend.exception.AccountNotFoundException;
import com.example.my_bank_backend.repositories.AccountRepository;

class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    private Account testAccount;
    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User();
        testUser.setId(1L);
        testAccount = new Account();
        testAccount.setId(1L);
        testAccount.setCpf("12345678909");
        testAccount.setCreditLimit(1000.0);
        testAccount.setAccountValue(0.0);
        testAccount.setUsedLimit(0.0);
        testAccount.setUser(testUser);
    }

    @Test
    void testAddValueToAccount() {
        testAccount = new Account();
        testAccount.setAccountValue(0.0);

        when(accountRepository.findByCpf("01234567899")).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(testAccount)).thenReturn(testAccount);

        Account updatedAccount = accountService.addValueToAccount("01234567899", 500.0);

        assertEquals(500.0, updatedAccount.getAccountValue());
        verify(accountRepository, times(1)).save(testAccount);
    }

    @Test
    void testCreateAccountForUser() {
        Account createdAccount = accountService.createAccountForUser("12345678909", testUser);

        assertEquals("12345678909", createdAccount.getCpf());
        assertEquals(1000.0, createdAccount.getCreditLimit());
        assertEquals(0.0, createdAccount.getAccountValue());
        assertEquals(0.0, createdAccount.getUsedLimit());
        assertEquals(testUser, createdAccount.getUser());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void testGetAccountByCpf() {
        when(accountRepository.findByCpf("12345678909")).thenReturn(Optional.of(testAccount));

        Account retrievedAccount = accountService.getAccountByCpf("12345678909");

        assertEquals(testAccount.getCpf(), retrievedAccount.getCpf());
        verify(accountRepository, times(1)).findByCpf("12345678909");
    }

    @Test
    void testGetAllAccounts() {
        when(accountRepository.findAll()).thenReturn(Collections.singletonList(testAccount));

        var accounts = accountService.getAllAccounts();

        assertEquals(1, accounts.size());
        assertEquals(testAccount.getCpf(), accounts.get(0).getCpf());
        verify(accountRepository, times(1)).findAll();
    }

    @Test
    void testSubValueAccount() {
        testAccount = new Account();
        testAccount.setAccountValue(500.0);

        when(accountRepository.findByCpf("12345678909")).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(testAccount)).thenReturn(testAccount);

        Account updatedAccount = accountService.subValueAccount("12345678909", 300.0);

        assertEquals(200.0, updatedAccount.getAccountValue());

        verify(accountRepository, times(1)).save(testAccount);
    }

    @Test
    void testSubValueAccountAccountNotFound() {
        when(accountRepository.findByCpf("invalid_cpf")).thenReturn(Optional.empty());

        try {
            accountService.subValueAccount("invalid_cpf", 300.0);
        } catch (AccountNotFoundException e) {
            assertEquals("Account not found for CPF: invalid_cpf", e.getMessage());
        }

        verify(accountRepository, times(1)).findByCpf("invalid_cpf");
    }

    @Test
    void testSubValueAccountInvalidValue() {
        when(accountRepository.findByCpf("12345678909")).thenReturn(Optional.of(testAccount));

        try {
            accountService.subValueAccount("12345678909", -100.0);
        } catch (IllegalArgumentException e) {
            assertEquals("Value must be greater than zero.", e.getMessage());
        }

        verify(accountRepository, never()).save(any());
    }

    @Test
    void testGetAccountByCpfNotFound() {
        when(accountRepository.findByCpf("invalid_cpf")).thenReturn(Optional.empty());

        try {
            accountService.getAccountByCpf("invalid_cpf");
        } catch (AccountNotFoundException e) {
            assertEquals("Account not Found!", e.getMessage());
        }

        verify(accountRepository, times(1)).findByCpf("invalid_cpf");
    }
}
