package com.example.my_bank_backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.my_bank_backend.domain.invoice.Invoice;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findById(Long id);
    // Você pode adicionar métodos de consulta personalizados aqui, se necessário
}