package com.example.my_bank_backend.service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.my_bank_backend.domain.account.Account;
import com.example.my_bank_backend.domain.invoice.Invoice;
import com.example.my_bank_backend.repositories.AccountRepository;
import com.example.my_bank_backend.repositories.InvoiceRepository;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final AccountRepository accountRepository;

    public InvoiceService(InvoiceRepository invoiceRepository, AccountRepository accountRepository) {
        this.invoiceRepository = invoiceRepository;
        this.accountRepository = accountRepository;
    }

    public ResponseEntity<List<Invoice>> getAllInvoices() {
        return ResponseEntity.ok(invoiceRepository.findAll());
    }

    public ResponseEntity<String> addValue(Long invoiceId, Double value) {
        Optional<Invoice> optInvoice = invoiceRepository.findById(invoiceId);

        if (optInvoice.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Invoice existingInvoice = optInvoice.get();
        Date invoiceDate = existingInvoice.getInvoiceDate();

        LocalDate invoiceLocalDate = new java.sql.Date(invoiceDate.getTime()).toLocalDate();
        int invoiceMonth = invoiceLocalDate.getMonthValue();
        int invoiceYear = invoiceLocalDate.getYear();

        List<Invoice> invoices = invoiceRepository.findByDateMonthAndYear(invoiceMonth, invoiceYear);

        Optional<Invoice> matchingInvoice = invoices.stream()
                .filter(invoice -> invoice.getId().equals(invoiceId))
                .findFirst();

        if (matchingInvoice.isEmpty()) {
            return ResponseEntity.badRequest().body("Invoice don't match with month and year.");
        }

        Double existingAmount = existingInvoice.getAmount();
        Double newAmount = existingAmount + value;

        existingInvoice.setAmount(newAmount);
        invoiceRepository.save(existingInvoice);

        return ResponseEntity.ok("Value added to invoice");
    }

    public ResponseEntity<Optional<Invoice>> getInvoiceByAccount(String accountCpf) {
        Optional<Account> optAccount = accountRepository.findByCpf(accountCpf);

        if (optAccount.isPresent()) {
            long accountId = optAccount.get().getId();
            LocalDate currentDate = LocalDate.now();
            int currentMonth = currentDate.getMonthValue();
            int currentYear = currentDate.getYear();

            List<Invoice> invoices = invoiceRepository.findByDateMonthAndYear(currentMonth, currentYear);

            Optional<Invoice> optInvoice = invoices.stream()
                    .filter(invoice -> invoice.getAccount().getId().equals(accountId))
                    .findFirst();

            if (optInvoice.isPresent()) {
                return ResponseEntity.ok(optInvoice);
            }
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.notFound().build();
    }

    public ResponseEntity<String> payInvoice(String accountCpf) {
        Optional<Account> optAccount = accountRepository.findByCpf(accountCpf);

        Account account = optAccount.get();

        if (optAccount.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Optional<Invoice> optInvoice = invoiceRepository.findInvoiceByAccountId(account.getId());

        Invoice payInvoice = optInvoice.get();

        if (optInvoice.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (account.getAccountValue() < payInvoice.getAmount()) {
            return ResponseEntity.badRequest().body("You don't have any founds to pay this Invoice");
        }

        account.setAccountValue(account.getAccountValue() - payInvoice.getAmount());
        account.setUsedLimit(account.getUsedLimit() - payInvoice.getAmount());
        payInvoice.setAmount(account.getUsedLimit());

        invoiceRepository.save(payInvoice);
        accountRepository.save(account);

        return ResponseEntity.ok("Invoice was payed");
    }
}
