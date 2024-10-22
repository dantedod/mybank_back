package com.example.my_bank_backend.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.my_bank_backend.domain.account.Account;
import com.example.my_bank_backend.exception.AccountNotFoundException;
import com.example.my_bank_backend.service.AccountService;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = {"http://localhost:4200", "https://mybank-front-3d93fg8dq-mateus-quixadas-projects.vercel.app"})
@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{cpf}")
    public ResponseEntity<Account> getAccountByCpf(@PathVariable String cpf) {

        try {

            if (cpf != null) {
                Account account = accountService.getAccountByCpf(cpf);
                return ResponseEntity.ok(account);
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        } catch (AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {

        try {
            List<Account> accounts = accountService.getAllAccounts();
            return ResponseEntity.ok(accounts);

        } catch (AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{cpf}/{value}")
    public ResponseEntity<Account> addValueToAccount(@PathVariable String cpf, @PathVariable Double value) {

        if (cpf == null || value == null || value <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        
        try {
            Account account = accountService.addValueToAccount(cpf, value);

            return ResponseEntity.ok(account);
        } catch (AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{cpf}/{value}/sub")
    public ResponseEntity<Account> subValueAccount(@PathVariable String cpf, @PathVariable Double value) {

        if (cpf == null || value == null || value <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            Account account = accountService.subValueAccount(cpf, value);

            return ResponseEntity.ok(account);
        } catch (AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
