package com.example.my_bank_backend.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.my_bank_backend.domain.transaction.Transaction;
import com.example.my_bank_backend.dto.TransactionRequestDto;
import com.example.my_bank_backend.dto.TransactionResponseDto;
import com.example.my_bank_backend.service.TransactionService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/transaction")
public class TransactionController {

    private TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/create")
    public ResponseEntity<TransactionResponseDto> createTransaction(
            @RequestBody TransactionRequestDto transactionRequestDto) {
        TransactionResponseDto response = transactionService.processTransaction(
                transactionRequestDto.cpfSender(),
                transactionRequestDto.cpfReceiver(),
                transactionRequestDto.amount(),
                transactionRequestDto.paymentDescription(),
                transactionRequestDto.transactionType());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<List<TransactionResponseDto>> getAllTransactionsByCpf(@PathVariable String cpf) {
        return transactionService.getAllTransactionsByCpf(cpf);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Transaction> getTransactionByCpf(@PathVariable Long id) {
        return transactionService.getTransactionByCpf(id);
    }
}
