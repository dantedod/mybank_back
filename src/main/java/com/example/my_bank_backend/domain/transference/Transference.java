package com.example.my_bank_backend.domain.transference;

import java.time.LocalDateTime;

import com.example.my_bank_backend.domain.account.Account;
import com.example.my_bank_backend.domain.enums.TransferenceEnum;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "transference")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Transference {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_account_id", nullable = false)
    private Account senderAccount;

    @ManyToOne
    @JoinColumn(name = "receiver_account_id", nullable = false)
    private Account receiverAccount;

    private LocalDateTime transferenceDate;
    private Double amount;
    private String paymentDescription;

    @Enumerated(EnumType.STRING)
    private TransferenceEnum transferenceType;

    @Transient
    public String getSenderAccountCpf() {
        return senderAccount != null ? senderAccount.getCpf() : null;
    }

    @Transient
    public String getReceiverAccountCpf() {
        return receiverAccount != null ? receiverAccount.getCpf() : null;
    }
}
