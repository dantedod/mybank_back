package com.example.my_bank_backend.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.my_bank_backend.domain.user.User;
import com.example.my_bank_backend.dto.ConfigDetailsDto;
import com.example.my_bank_backend.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountService accountService;


    public ConfigDetailsDto updateUser(ConfigDetailsDto requestDto) {
        User user = userRepository.findByCpf(requestDto.cpf()).orElse(null);

        if (user == null) {
            return null;
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
            return requestDto;
        } else {
            return null;
        }
    }

    public User getUserByCpf(String cpf) {
        Optional<User> optUser = this.userRepository.findByCpf(cpf);

        if (optUser.isPresent()) {
            return optUser.get();
        } else {
            return null;
        }
    }

    public User register(String name, String email, String password, String phone, String cpf,
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

            newUser.setAccount(accountService.createAccountForUser(cpf, newUser));

            this.userRepository.save(newUser);
            return newUser;
        }
        return null;
    }

}
