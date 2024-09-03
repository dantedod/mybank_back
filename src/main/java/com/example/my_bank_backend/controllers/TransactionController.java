package com.example.my_bank_backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.my_bank_backend.domain.enums.TransactionEnum;
import com.example.my_bank_backend.dto.TransactionRequestDto;
import com.example.my_bank_backend.service.TransactionService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/transaction")
public class TransactionController {

    private TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService){
        this.transactionService = transactionService;
    } 

    @PostMapping("/create")
    public ResponseEntity<String> createTransaction(@RequestBody TransactionRequestDto transactionRequestDto) {
        try{
            TransactionEnum transactionType = transactionRequestDto.transactionType();
            transactionService.processTransaction(
                transactionRequestDto.senderAccountId(),
                transactionRequestDto.receiverAccountId(),
                transactionRequestDto.amount(),
                transactionRequestDto.paymentDescription(),
                transactionType
            );
            return ResponseEntity.ok("Successful transaction creation");
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body("Unable to create a transaction " + e);
        }
    }
}
