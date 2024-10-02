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

import com.example.my_bank_backend.domain.transference.Transference;
import com.example.my_bank_backend.dto.TransferRequestDto;
import com.example.my_bank_backend.dto.TransferResponseDto;
import com.example.my_bank_backend.service.TransferenceService;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/transference")
@RequiredArgsConstructor
public class TransferenceController {

    private final TransferenceService transferenceService;

    @PostMapping("/create")
    public ResponseEntity<TransferResponseDto> createTransaction(
            @RequestBody TransferRequestDto transferenceRequestDto) {
        TransferResponseDto response = transferenceService.processTransfer(
                transferenceRequestDto.cpfSender(),
                transferenceRequestDto.cpfReceiver(),
                transferenceRequestDto.amount(),
                transferenceRequestDto.paymentDescription(),
                transferenceRequestDto.transferenceType());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<List<TransferResponseDto>> getAllTransferencesByCpf(@PathVariable String cpf) {
        return transferenceService.getAllTransfersByCpf(cpf);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Transference> getTransferenceByCpf(@PathVariable Long id) {
        return transferenceService.getTransferencesByCpf(id);
    }
}
