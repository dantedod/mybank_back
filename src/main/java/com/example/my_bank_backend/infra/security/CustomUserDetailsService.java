package com.example.my_bank_backend.infra.security;

import java.util.ArrayList;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.example.my_bank_backend.domain.user.User;
import com.example.my_bank_backend.repositories.UserRepository;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = this.userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario não encontrado!"));
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
                new ArrayList<>());

    }

}
