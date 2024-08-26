package com.example.my_bank_backend.repositories;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.my_bank_backend.domain.card.Card;
import com.example.my_bank_backend.domain.invoice.Invoice;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {


    List<Invoice> findByCardAndInvoiceStatus(Card card, String invoiceStatus);

    Optional<Invoice> findByDate(Date date);
  }