package com.example.my_bank_backend.domain.transaction;

import java.time.LocalDateTime;

import com.example.my_bank_backend.domain.account.Account;
import com.example.my_bank_backend.domain.enums.TransactionEnum;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_account_id", nullable = false)
    private Account senderAccountId;

    @ManyToOne
    @JoinColumn(name = "receiver_account_id", nullable = false)
    private Account receiverAccountId;

    private LocalDateTime transactionDate;
    private Double amount;
    private String paymentDescription;

    @Enumerated(EnumType.STRING)
    private TransactionEnum transactionType;

}
