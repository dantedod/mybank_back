package com.example.my_bank_backend.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.my_bank_backend.domain.user.User;
import com.example.my_bank_backend.dto.ConfigDetailsDto;
import com.example.my_bank_backend.repositories.UserRepository;
import com.example.my_bank_backend.service.UserService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/user")
public class UserController {

  private final UserRepository userRepository;
  private final UserService userService;

  @Autowired
  public UserController(UserRepository userRepository, UserService userService){
    this.userRepository = userRepository;
    this.userService = userService;
  }

  @GetMapping
  public ResponseEntity<String> getUser() {
    return ResponseEntity.ok("Sucesso!");
  }

  @GetMapping("/{cpf}")
  public ResponseEntity<User> getUserByCpf(@PathVariable String cpf) {
    Optional<User> optUser = this.userRepository.findByCpf(cpf);

    if (optUser.isPresent()) {
      return ResponseEntity.ok(optUser.get());
    } else {
      return ResponseEntity.notFound().build();
    }

  }

    @PutMapping("/update")
    public ResponseEntity<ConfigDetailsDto> updateUser(@RequestBody ConfigDetailsDto requestDto) {
        boolean result = userService.updateUser(requestDto);
        if (result) {
            return ResponseEntity.ok(requestDto);
        } else {
            return ResponseEntity.notFound().build(); 
        }
    }
  
}
