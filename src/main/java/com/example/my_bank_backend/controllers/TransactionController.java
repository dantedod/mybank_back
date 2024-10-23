package com.example.my_bank_backend.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.my_bank_backend.dto.TransactionRequestDto;
import com.example.my_bank_backend.dto.TransactionResponseDto;
import com.example.my_bank_backend.service.TransactionService;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = {"http://localhost:4200", "https://mybank-front.vercel.app"})
@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/create")
    public ResponseEntity<TransactionResponseDto> createTransaction(
            @RequestBody TransactionRequestDto transactionRequestDto) {

        if(transactionRequestDto == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            TransactionResponseDto response = transactionService.processTransaction(
                transactionRequestDto.accountId(),
                transactionRequestDto.cardId(),
                transactionRequestDto.amount(),
                transactionRequestDto.paymentDescription());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<List<TransactionResponseDto>> getAllTransactionsByCpf(@PathVariable String cpf) {

        if(cpf == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            List<TransactionResponseDto> transactions = transactionService.getAllTransactionsByCpf(cpf);
            
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        
    }
}
