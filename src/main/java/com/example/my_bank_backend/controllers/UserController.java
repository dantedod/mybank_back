package com.example.my_bank_backend.controllers;

import org.springframework.http.HttpStatus;
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

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping("/{cpf}")
  public ResponseEntity<User> getUserByCpf(@PathVariable String cpf) {

    if (cpf == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    try {
      User user = userService.getUserByCpf(cpf);

      return ResponseEntity.ok(user);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

  }

  @PutMapping("/update")
  public ResponseEntity<ConfigDetailsDto> updateUser(@RequestBody ConfigDetailsDto requestDto) {

    if (requestDto == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

    }

    try {
      ConfigDetailsDto upDto = userService.updateUser(requestDto);
      return ResponseEntity.ok(upDto);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    
  }

}
