package com.example.my_bank_backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.my_bank_backend.domain.card.Card;
import com.example.my_bank_backend.domain.invoice.Invoice;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

  List<Invoice> findByCardAndInvoiceStatus(Card card, String invoiceStatus);

  @Query("SELECT i FROM Invoice i WHERE EXTRACT(MONTH FROM i.invoiceDate) = :month AND EXTRACT(YEAR FROM i.invoiceDate) = :year")
  List<Invoice> findByDateMonthAndYear(@Param("month") int month, @Param("year") int year);

  @Query("SELECT COUNT(i) > 0 FROM Invoice i WHERE i.card = :card AND EXTRACT(MONTH FROM i.invoiceDate) = :month AND EXTRACT(YEAR FROM i.invoiceDate) = :year")
  boolean existsByCardAndMonthAndYear(@Param("card") Card card, @Param("month") int month, @Param("year") int year);

  boolean existsByCard(Card card);

  Optional<Invoice> findInvoiceByCardId(Long cardId);

    List<Invoice> findInvoicesByCardId(Long cardId);

}