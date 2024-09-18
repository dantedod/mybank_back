package com.example.my_bank_backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.my_bank_backend.domain.account.Account;
import com.example.my_bank_backend.service.AccountService;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService){
        this.accountService = accountService;
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<Account> getAccountByCpf(@PathVariable String cpf) {
        return accountService.getAccountByCpf(cpf);
    }

    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @PostMapping("/{cpf}/{value}")
    public ResponseEntity<Account> addValueToAccount(@PathVariable String cpf, @PathVariable Double value) {
        return accountService.addValueToAccount(cpf, value);
    }

    @PostMapping("/{cpf}/{value}/sub")
    public ResponseEntity<Account> subValueAccount(@PathVariable String cpf, @PathVariable Double value) {
        return accountService.subValueAccount(cpf, value);
    }

}
