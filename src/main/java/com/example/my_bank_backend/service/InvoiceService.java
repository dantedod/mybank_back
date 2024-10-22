package com.example.my_bank_backend.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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

    public List<Invoice> getAllInvoices(String accountCpf) {
        return invoiceRepository.findByAccountCpf(accountCpf);
    }

    public String createInvoice(Account account, Double purchaseAmount, int invoiceMonth, int invoiceYear) {
        if (account == null) {
            return "Account is null";
        }
        
        Optional<Invoice> existsInvoice = invoiceRepository.findByAccountAndMonthAndYear(account, invoiceMonth, invoiceYear);
        
        if (existsInvoice.isEmpty()) {
            Invoice newInvoice = new Invoice();
            newInvoice.setAccount(account);
            newInvoice.setAmount(purchaseAmount);
            newInvoice.setEmail(account.getUser().getEmail());
            newInvoice.setInvoiceDate(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            newInvoice.setDueDate(Date.from(LocalDate.now().plusDays(30).atStartOfDay(ZoneId.systemDefault()).toInstant()));
            newInvoice.setClosingDate(Date.from(LocalDate.now().plusDays(24).atStartOfDay(ZoneId.systemDefault()).toInstant()));
            newInvoice.setInvoiceStatus("Unpaid!");
            newInvoice.setInvoiceDescription("Month: " + invoiceMonth);
            
            try {
                invoiceRepository.save(newInvoice);
                return "SUCCESS";
            } catch (Exception e) {
                return "Failed to create invoice: " + e.getMessage();
            }
        } else {
            Invoice existingInvoice = existsInvoice.get();
            existingInvoice.setAmount(existingInvoice.getAmount() + purchaseAmount);
            invoiceRepository.save(existingInvoice);
            return "Invoice updated";
        }
    }
    

    public String addValue(Long invoiceId, Double value) {
        Optional<Invoice> optInvoice = invoiceRepository.findById(invoiceId);

        if (optInvoice.isEmpty()) {
            return "Invoice not Found!";
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
            return "Invoice don't match with month and year.";
        }

        Double existingAmount = existingInvoice.getAmount();
        Double newAmount = existingAmount + value;

        existingInvoice.setAmount(newAmount);
        invoiceRepository.save(existingInvoice);

        return "Value added to invoice";
    }

    public Invoice getInvoiceByAccount(String accountCpf) {
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
                return optInvoice.get();
            }
        }
        return null;
    }

    public Invoice payInvoice(String accountCpf, Double payValue) {

        Optional<Account> optAccount = accountRepository.findByCpf(accountCpf);
        if (optAccount.isEmpty()) {
            return null;
        }
    
        Account account = optAccount.get();
    
        Optional<Invoice> optInvoice = invoiceRepository.findInvoiceByAccountId(account.getId());
        if (optInvoice.isEmpty()) {
            return null;
        }

        Invoice payInvoice = optInvoice.get();
    
        LocalDate actualDay = LocalDate.now();
        Date payDay = Date.from(actualDay.atStartOfDay(ZoneId.systemDefault()).toInstant());
    
        if (payDay.compareTo(payInvoice.getDueDate()) <= 0) {

            account.setAccountValue(account.getAccountValue() - payValue);
            payInvoice.setAmount(payInvoice.getAmount() - payValue);
            
            account.setUsedLimit(account.getUsedLimit() - payValue);
        }
    
        invoiceRepository.save(payInvoice);
        accountRepository.save(account);
    
        return payInvoice;
    }
    
}
