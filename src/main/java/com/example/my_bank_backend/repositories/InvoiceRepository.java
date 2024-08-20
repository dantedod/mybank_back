package com.example.my_bank_backend.repositories;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.my_bank_backend.domain.invoice.Invoice;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
}