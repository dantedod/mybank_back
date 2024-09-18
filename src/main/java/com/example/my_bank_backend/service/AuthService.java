package com.example.my_bank_backend.service;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.my_bank_backend.domain.account.Account;
import com.example.my_bank_backend.domain.user.User;
import com.example.my_bank_backend.dto.ResponseDto;
import com.example.my_bank_backend.infra.security.TokenService;
import com.example.my_bank_backend.repositories.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenService tokenServic) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenServic;
    }

    public ResponseEntity<ResponseDto> login(String email, String password) {
        User user = this.userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));
        if (passwordEncoder.matches(password, user.getPassword())) {
            String token = this.tokenService.generateToken(user);
            return ResponseEntity.ok(new ResponseDto(token, user.getName(), user.getCpf()));
        }
        return ResponseEntity.badRequest().build();
    }

    public ResponseEntity<ResponseDto> register(String name, String email, String password, String phone, String cpf,
            String birthDate) {

        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            User newUser = new User();
            newUser.setPassword(passwordEncoder.encode(password));
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setPhone(phone);
            newUser.setCpf(cpf);
            newUser.setBirthdate(birthDate);

            Account newAccount = new Account();
            newAccount.setCpf(cpf);
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
