package com.example.my_bank_backend.service;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

@Service
public class CardService {

    private final SecureRandom secureRandom = new SecureRandom();
    private final Set<String> generatedCards = new HashSet<>();
    private final Set<String> generatedCvv = new HashSet<>();

    public String generateCardNumber() {
        String cardNumber;
        do {
            cardNumber = String.format("%04d %04d %04d %04d",
                    secureRandom.nextInt(10000),
                    secureRandom.nextInt(10000),
                    secureRandom.nextInt(10000),
                    secureRandom.nextInt(10000));
        } while (generatedCards.contains(cardNumber));

        generatedCards.add(cardNumber);
        return cardNumber;
    }

    public String generateCvv() {
        String cvv;
        do {
            cvv = String.format("%03d", secureRandom.nextInt(1000));
        } while (generatedCvv.contains(cvv));

        generatedCvv.add(cvv);
        return cvv;
    }
}
