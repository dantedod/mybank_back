package com.example.my_bank_backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.my_bank_backend.domain.account.Account;
import com.example.my_bank_backend.domain.enums.TransferenceEnum;
import com.example.my_bank_backend.domain.transference.Transference;
import com.example.my_bank_backend.dto.TransferResponseDto;
import com.example.my_bank_backend.repositories.AccountRepository;
import com.example.my_bank_backend.repositories.TransferenceRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransferenceService {

    private final AccountRepository accountRepository;
    private final TransferenceRepository transferenceRepository;

    @Transactional
    public TransferResponseDto processTransfer(String cpfSender, String cpfReceiver, Double amount,
            String paymentDescription, TransferenceEnum transferenceType) {

        Account senderAccount = accountRepository.findByCpf(cpfSender)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));

        Account receiverAccount = accountRepository.findByCpf(cpfReceiver)
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        if (senderAccount.getAccountValue() < amount) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        senderAccount.setAccountValue(senderAccount.getAccountValue() - amount);
        receiverAccount.setAccountValue(receiverAccount.getAccountValue() + amount);

        accountRepository.save(senderAccount);
        accountRepository.save(receiverAccount);

        Transference transfer = new Transference();
        transfer.setSenderAccount(senderAccount);
        transfer.setReceiverAccount(receiverAccount);
        transfer.setAmount(amount);
        transfer.setPaymentDescription(paymentDescription);
        transfer.setTransferenceDate(LocalDateTime.now());
        transfer.setTransferenceType(transferenceType);

        Transference savedTransfer = transferenceRepository.save(transfer);

        return new TransferResponseDto(
                savedTransfer.getId(),
                senderAccount.getCpf(),
                senderAccount.getUser().getName(),
                receiverAccount.getCpf(),
                receiverAccount.getUser().getName(),
                savedTransfer.getAmount(),
                savedTransfer.getPaymentDescription(),
                savedTransfer.getTransferenceDate(),
                savedTransfer.getTransferenceType());
    }

    public ResponseEntity<List<TransferResponseDto>> getAllTransfersByCpf(String cpf) {
        Optional<Account> optAccount = accountRepository.findByCpf(cpf);
    
        if (optAccount.isPresent()) {
            Account account = optAccount.get();
            Long accountId = account.getId(); // Obter o ID da conta
    
            List<Transference> transferences = transferenceRepository.findBySenderAccountIdOrReceiverAccountId(accountId, accountId);
    
            if (transferences.isEmpty()) {
                return ResponseEntity.noContent().build();
            } else {
                List<TransferResponseDto> responseDtos = transferences.stream()
                        .map(tx -> new TransferResponseDto(
                                tx.getId(),
                                tx.getSenderAccountCpf(),
                                tx.getSenderAccount().getUser().getName(),
                                tx.getReceiverAccountCpf(),
                                tx.getReceiverAccount().getUser().getName(),
                                tx.getAmount(),
                                tx.getPaymentDescription(),
                                tx.getTransferenceDate(),
                                tx.getTransferenceType()))
                        .collect(Collectors.toList());
                return ResponseEntity.ok(responseDtos);
            }
        } else {
            return ResponseEntity.noContent().build();
        }
    }
    

    public ResponseEntity<Transference> getTransferencesByCpf(Long id) {

        Optional<Transference> transaction = transferenceRepository.findById(id);

        if (transaction.isPresent()) {
            return ResponseEntity.ok(transaction.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
