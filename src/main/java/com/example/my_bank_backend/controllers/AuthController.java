package com.example.my_bank_backend.controllers;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.my_bank_backend.domain.account.Account;
import com.example.my_bank_backend.domain.user.User;
import com.example.my_bank_backend.dto.LoginRequestDto;
import com.example.my_bank_backend.dto.RegisterRequestDto;
import com.example.my_bank_backend.dto.ResponseDto;
import com.example.my_bank_backend.infra.security.TokenService;
import com.example.my_bank_backend.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<ResponseDto> login(@RequestBody LoginRequestDto body) {
        User user = this.userRepository.findByEmail(body.email())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));
        if (passwordEncoder.matches(body.password(), user.getPassword())) {
            String token = this.tokenService.generateToken(user);
            return ResponseEntity.ok(new ResponseDto(token, user.getName(), user.getCpf()));
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseDto> register(@RequestBody RegisterRequestDto body) {

        Optional<User> user = this.userRepository.findByEmail(body.email());

        if (user.isEmpty()) {
            User newUser = new User();
            newUser.setPassword(passwordEncoder.encode(body.password()));
            newUser.setEmail(body.email());
            newUser.setName(body.name());
            newUser.setPhone(body.phone());
            newUser.setCpf(body.cpf());
            newUser.setBirthdate(body.birthdate());

            Account newAccount = new Account();
            newAccount.setCpf(body.cpf());
            newAccount.setCreditLimit(1000.0);
            newAccount.setAccountValue(0.0);
            newAccount.setUsedLimit(0.0);

            newAccount.setUser(newUser);
            newUser.setAccount(newAccount);

            this.userRepository.save(newUser);
            String token = this.tokenService.generateToken(newUser);
            return ResponseEntity.ok(new ResponseDto(token, newUser.getName(), newUser.getCpf()));
        }
        return ResponseEntity.badRequest().build();
    }

}
