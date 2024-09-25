package com.example.my_bank_backend.service;


import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.my_bank_backend.domain.user.User;
import com.example.my_bank_backend.dto.ResponseDto;
import com.example.my_bank_backend.infra.security.TokenService;
import com.example.my_bank_backend.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public ResponseEntity<ResponseDto> login(String email, String password) {
        User user = this.userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));
        if (passwordEncoder.matches(password, user.getPassword())) {
            String token = this.tokenService.generateToken(user);
            return ResponseEntity.ok(new ResponseDto(token, user.getName(), user.getCpf()));
        }
        return ResponseEntity.badRequest().build();
    }

}
