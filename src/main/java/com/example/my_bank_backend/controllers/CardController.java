package com.example.my_bank_backend.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.my_bank_backend.domain.card.Card;
import com.example.my_bank_backend.dto.CardRequestDto;
import com.example.my_bank_backend.exception.CardAlreadyExistsException;
import com.example.my_bank_backend.exception.CardDisabledException;
import com.example.my_bank_backend.exception.CardWasDisableException;
import com.example.my_bank_backend.exception.ExceedAccountLimitException;
import com.example.my_bank_backend.exception.ExceedActualAccountLimitException;
import com.example.my_bank_backend.exception.InsufficientCardValueException;
import com.example.my_bank_backend.exception.InsufficientLimitException;
import com.example.my_bank_backend.service.CardService;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = { "http://localhost:4200", "https://mybank-front.vercel.app" })
@RestController
@RequestMapping("/card")
@RequiredArgsConstructor
public class CardController {

  private final CardService cardService;

  @PostMapping("/create")
  public ResponseEntity<String> createCard(@RequestBody CardRequestDto cardRequestDto) {
    try {
      Card card = cardService.createCard(cardRequestDto.accountCpf(), cardRequestDto.cardName(),
          cardRequestDto.cardPassword(), cardRequestDto.cardValue());

      if (card != null) {
        return ResponseEntity.ok("{\"message\":\"Cartão criado com sucesso\"}");
      }

      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao criar o cartão.");
    } catch (CardAlreadyExistsException ca) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cartão já existe.");
    } catch (ExceedAccountLimitException eal) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(eal.getMessage());
    } catch (ExceedActualAccountLimitException ecal) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ecal.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado.");
    }
  }

  @GetMapping("/{accountCpf}")
  public ResponseEntity<List<Card>> getCardByAccountCpf(@PathVariable String accountCpf) {

    try {
      List<Card> cards = cardService.getCardByAccountCpf(accountCpf);

      return ResponseEntity.ok(cards);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
  }

  @PostMapping("/buy/{accountCpf}/{cardId}/{purchaseAmount}")
  public ResponseEntity<Map<String, String>> buyWithCard(@PathVariable Long cardId, @PathVariable String accountCpf,
      @PathVariable Double purchaseAmount) {

    Map<String, String> response = new HashMap<>();
    try {
      String buy = cardService.buyWithCard(cardId, accountCpf, purchaseAmount);

      response.put("message", "Compra realizada com sucesso!");
      response.put("buyDetails", buy);

      return ResponseEntity.ok(response);

    }catch(CardDisabledException cde){
      response.put("error",cde.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }   catch (CardWasDisableException | InsufficientCardValueException | InsufficientLimitException cwd) {
      response.put("error", cwd.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

    }catch (Exception e) {
      response.put("error", "Erro inesperado ao realizar a compra.");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    
    }

  }

  @PostMapping("/disable/{cardId}")
  public ResponseEntity<Map<String, String>> disableCard(@PathVariable Long cardId) {

    try {
      cardService.disableCard(cardId);
      Map<String, String> response = new HashMap<>();
      response.put("message", "Card deleted!");
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      Map<String, String> response = new HashMap<>();
      response.put("error", "Card not found.");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
  }

}
