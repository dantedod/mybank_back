package com.example.my_bank_backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.my_bank_backend.domain.account.Account;
import com.example.my_bank_backend.domain.user.User;
import com.example.my_bank_backend.exception.AccountNotFoundException;
import com.example.my_bank_backend.repositories.AccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public Account getAccountByCpf(String cpf) {
        return accountRepository.findByCpf(cpf)
                .orElseThrow(() -> new AccountNotFoundException("Account not Found!"));
    }

    public Account createAccountForUser(String cpf, User newUser) {
        Account newAccount = new Account();
        newAccount.setCpf(cpf);
        newAccount.setCreditLimit(1000.0);
        newAccount.setAccountValue(0.0);
        newAccount.setUsedLimit(0.0);
        newAccount.setUser(newUser);
        return newAccount;
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Account addValueToAccount(String cpf, Double value) {
        
        if (value != null) {
            Optional<Account> optAccount = accountRepository.findByCpf(cpf);

            Account account = optAccount.get();
            account.setAccountValue(account.getAccountValue() + value);

            return accountRepository.save(account);
        }

        return null;
    }

    public Account subValueAccount(String cpf, Double value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Value must be greater than zero.");
        }

        Account account = accountRepository.findByCpf(cpf)
                .orElseThrow(() -> new AccountNotFoundException("Account not found for CPF: " + cpf));

        account.setAccountValue(account.getAccountValue() - value);

        return accountRepository.save(account);
    }

}
