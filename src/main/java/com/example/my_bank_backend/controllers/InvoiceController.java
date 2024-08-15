package com.example.my_bank_backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.my_bank_backend.domain.card.Card;
import com.example.my_bank_backend.domain.invoice.Invoice;
import com.example.my_bank_backend.repositories.InvoiceRepository;

@RestController
@RequestMapping("/invoice")
public class InvoiceController {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @GetMapping 
    public ResponseEntity<List<Invoice>> getAllInvoices() {
        return ResponseEntity.ok(invoiceRepository.findAll());
    }

    @PostMapping("/{card_id}/create")
    public ResponseEntity<Invoice> createInvoice(@PathVariable Card card_id,@RequestBody Invoice invoice) {
        Invoice savedInvoice = invoiceRepository.save(invoice);
        return ResponseEntity.ok(savedInvoice);
    }

    // Outros endpoints conforme necessário
}