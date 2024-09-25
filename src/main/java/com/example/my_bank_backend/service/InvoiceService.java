package com.example.my_bank_backend.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.my_bank_backend.domain.account.Account;
import com.example.my_bank_backend.domain.invoice.Invoice;
import com.example.my_bank_backend.repositories.AccountRepository;
import com.example.my_bank_backend.repositories.InvoiceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final AccountRepository accountRepository;

    public ResponseEntity<List<Invoice>> getAllInvoices() {
        return ResponseEntity.ok(invoiceRepository.findAll());
    }

    public ResponseEntity<String> createInvoice(Account account, Double purchaseAmount, int invoiceMonth, int invoiceYear) {
        Optional<Invoice> existsInvoice = invoiceRepository.findByAccountAndMonthAndYear(account, invoiceMonth, invoiceYear);
        
        if (existsInvoice.isEmpty()) {
            // Criação de uma nova fatura
            Invoice newInvoice = new Invoice();
            newInvoice.setAccount(account);
            newInvoice.setAmount(purchaseAmount);
            newInvoice.setEmail(account.getUser().getEmail());
            newInvoice.setInvoiceDate(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            newInvoice.setDueDate(Date.from(LocalDate.now().plusDays(30).atStartOfDay(ZoneId.systemDefault()).toInstant()));
            newInvoice.setClosingDate(Date.from(LocalDate.now().plusDays(24).atStartOfDay(ZoneId.systemDefault()).toInstant()));
            newInvoice.setInvoiceStatus("Não paga!");
            newInvoice.setInvoiceDescription("Fatura do mês: " + invoiceMonth);
            
            invoiceRepository.save(newInvoice);
            return ResponseEntity.ok("Invoice created");
        } else {
            // Atualização da fatura existente
            Invoice existingInvoice = existsInvoice.get();
            existingInvoice.setAmount(existingInvoice.getAmount() + purchaseAmount);
            invoiceRepository.save(existingInvoice);
            return ResponseEntity.ok("Invoice updated");
        }
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

    public ResponseEntity<String> payInvoice(String accountCpf, Double payValue) {
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
        LocalDate actualDay = LocalDate.now();
        Date payDay = Date.from(actualDay.atStartOfDay(ZoneId.systemDefault()).toInstant());

        if(payDay.compareTo(payInvoice.getDueDate()) <= 0){
          account.setAccountValue(account.getAccountValue() - payValue);
          payInvoice.setAmount(payInvoice.getAmount() - payValue);
          account.setUsedLimit(payInvoice.getAmount());
        }

        invoiceRepository.save(payInvoice);
        accountRepository.save(account);

        return ResponseEntity.ok("Invoice was payed");
    }
}
