package com.example.my_bank_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.my_bank_backend.domain.user.User;
import com.example.my_bank_backend.dto.ConfigDetailsDto;
import com.example.my_bank_backend.repositories.UserRepository;

@Service
public class UserService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean updateUser(ConfigDetailsDto requestDto) {
        User user = userRepository.findByCpf(requestDto.cpf()).orElse(null);
        if (user == null) {
            return false;
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
        }

        return updated;
    }
}
