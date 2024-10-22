package com.example.my_bank_backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.my_bank_backend.domain.user.User;
import com.example.my_bank_backend.dto.LoginRequestDto;
import com.example.my_bank_backend.dto.RegisterRequestDto;
import com.example.my_bank_backend.dto.ResponseDto;
import com.example.my_bank_backend.exception.IncorrectPasswordException;
import com.example.my_bank_backend.exception.UserNotFoundException;
import com.example.my_bank_backend.service.AuthenticationService;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<ResponseDto> login(@RequestBody LoginRequestDto body) {

        try {
            ResponseDto responseDto = authenticationService.login(body);

            if (responseDto != null) {
                return ResponseEntity.ok(responseDto);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        } catch (UserNotFoundException | IncorrectPasswordException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseDto> register(@RequestBody RegisterRequestDto body) {

        try {
            User user = authenticationService.register(body);

            if (user != null) {
                return ResponseEntity.ok(new ResponseDto(null, user.getName(), user.getCpf()));
            }
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .build();
        }
    }
}