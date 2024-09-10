package com.example.my_bank_backend.domain.invoice;

import java.util.Date;

import com.example.my_bank_backend.domain.card.Card;
import com.example.my_bank_backend.dto.InvoiceRequestDto;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
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
@Table(name = "invoice")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String invoiceDescription;
    private Double amount;
    private String cardName;
    private Date invoiceDate;
    private String invoiceStatus;
    private Date dueDate;
    private String email;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "card_id", referencedColumnName = "id", nullable = false)
    @JsonBackReference
    private Card card;

    public Invoice(InvoiceRequestDto invoiceRequestDto){
        this.invoiceDescription = invoiceRequestDto.invoiceDescription();
        this.amount = invoiceRequestDto.amount();
        this.cardName = invoiceRequestDto.cardName();
        this.invoiceDate = invoiceRequestDto.invoiceDate();
        this.invoiceStatus = invoiceRequestDto.invoiceStatus();
        this.dueDate = invoiceRequestDto.dueDate();
        this.email = invoiceRequestDto.email();
    }
}
