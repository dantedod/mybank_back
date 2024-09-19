package com.example.my_bank_backend.service;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.my_bank_backend.domain.user.User;
import com.example.my_bank_backend.dto.ConfigDetailsDto;
import com.example.my_bank_backend.repositories.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<ConfigDetailsDto> updateUser(ConfigDetailsDto requestDto) {
        User user = userRepository.findByCpf(requestDto.cpf()).orElse(null);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        boolean updated = false;

        if (user.getPhone() != null && !user.getPhone().equals(requestDto.phone())) {
            user.setPhone(requestDto.phone());
            updated = true;
        }

        if (user.getEmail() != null && !user.getEmail().equals(requestDto.email())) {
            user.setEmail(requestDto.email());
            updated = true;
        }

        if (user.getPassword() != null && !user.getPassword().equals(requestDto.password())) {
            user.setPassword(passwordEncoder.encode(requestDto.password()));
            updated = true;
        }

        if (updated) {
            userRepository.save(user);
            return ResponseEntity.ok(requestDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<User> getUserByCpf(String cpf) {
        Optional<User> optUser = this.userRepository.findByCpf(cpf);

        if (optUser.isPresent()) {
            return ResponseEntity.ok(optUser.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
