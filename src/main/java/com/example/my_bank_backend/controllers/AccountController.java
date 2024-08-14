package com.example.my_bank_backend.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.my_bank_backend.domain.account.Account;
import com.example.my_bank_backend.domain.user.User;
import com.example.my_bank_backend.repositories.AccountRepository;
import com.example.my_bank_backend.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {
    
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<String> createAccount(@RequestBody Account account) {
        Optional<User> optUser = userRepository.findByCpf(account.getCpf());

        if(optUser.isPresent()) {
            User user = optUser.get();

            if(user.getAccount() != null) {
                return ResponseEntity.badRequest().body("Já existe uma conta com esse CPF");
            }

            account.setUser(user);
            accountRepository.save(account);
            return ResponseEntity.ok("Conta criada com sucesso");
        } else {
            return ResponseEntity.badRequest().body("Fudeu, usuário não encontrado!");
        }  
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<Account> getAccountByCpf(@PathVariable String cpf) {
        Optional<Account> optAccount = accountRepository.findByCpf(cpf);

        return optAccount.map(ResponseEntity:: ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts(){
        List<Account> accounts = accountRepository.findAll();
        return ResponseEntity.ok(accounts);
    }

    @PostMapping("/{cpf}/{value}")
    public ResponseEntity<Account> addValueToAccount(@PathVariable String cpf, @PathVariable Double value){

        Optional<Account> optAccount = accountRepository.findByCpf(cpf);

        if (value == null) {
            return ResponseEntity.badRequest().body(null);  
        }

        Account account = optAccount.get();
        account.setAccountValue(account.getAccountValue() + value);

        Account updatedAccount = accountRepository.save(account);

        return ResponseEntity.ok(updatedAccount);
    }

    @PostMapping("/{cpf}/{value}/sub")
    public ResponseEntity<Account> subValueAccount(@PathVariable String cpf, @PathVariable Double value){

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
