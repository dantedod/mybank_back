package com.example.my_bank_backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.my_bank_backend.domain.account.Account;
import com.example.my_bank_backend.repositories.AccountRepository;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public ResponseEntity<Account> getAccountByCpf(String cpf) {
        Optional<Account> optAccount = accountRepository.findByCpf(cpf);

        return optAccount.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    public ResponseEntity<List<Account>> getAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        return ResponseEntity.ok(accounts);
    }

    public ResponseEntity<Account> addValueToAccount(String cpf, Double value) {
        Optional<Account> optAccount = accountRepository.findByCpf(cpf);

        if (value == null) {
            return ResponseEntity.badRequest().body(null);
        }

        Account account = optAccount.get();
        account.setAccountValue(account.getAccountValue() + value);

        Account updatedAccount = accountRepository.save(account);

        return ResponseEntity.ok(updatedAccount);
    }

    public ResponseEntity<Account> subValueAccount(String cpf, Double value) {
        Optional<Account> optAccount = accountRepository.findByCpf(cpf);

        if (value == null) {
            return ResponseEntity.badRequest().body(null);
        }

        Account account = optAccount.get();
        account.setAccountValue(account.getAccountValue() - value);

        Account updatedAccount = accountRepository.save(account);

        return ResponseEntity.ok(updatedAccount);
    }
}
