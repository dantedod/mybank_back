package com.example.my_bank_backend.controllers;

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
import com.example.my_bank_backend.service.UserService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/user")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/{cpf}")
  public ResponseEntity<User> getUserByCpf(@PathVariable String cpf) {
    return userService.getUserByCpf(cpf);
  }

  @PutMapping("/update")
  public ResponseEntity<ConfigDetailsDto> updateUser(@RequestBody ConfigDetailsDto requestDto) {
    return userService.updateUser(requestDto);
  }

}
