package com.example.my_bank_backend.domain.card;

import com.example.my_bank_backend.domain.account.Account;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cards")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Card {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String card_name;
    private String card_number;
    private String card_password;
    private Integer cvv;
    private Double card_value;
    private String expiration_date;
    private String card_status;
    private Long account_id;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "card", orphanRemoval = true)
    @JoinColumn(name = "account_id", referencedColumnName = "id", insertable = false, updatable = false)
    @JsonManagedReference
    private Account account;
}
