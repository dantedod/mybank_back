package com.example.my_bank_backend.service;

import org.springframework.stereotype.Service;

import com.example.my_bank_backend.domain.user.User;
import com.example.my_bank_backend.dto.LoginRequestDto;
import com.example.my_bank_backend.dto.RegisterRequestDto;
import com.example.my_bank_backend.dto.ResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthService authService;
    private final UserService userService;

    public ResponseDto login(LoginRequestDto body) {
        return authService.login(body.email(), body.password());
    }

    public User register(RegisterRequestDto body) {
        return userService.register(body.name(), body.email(), body.password(), body.phone(), body.cpf(), body.birthdate());
    }
}
