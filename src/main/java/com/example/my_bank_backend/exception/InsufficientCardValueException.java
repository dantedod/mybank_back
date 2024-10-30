package com.example.my_bank_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.experimental.StandardException;

@ResponseStatus(HttpStatus.BAD_REQUEST)
@StandardException
public class InsufficientCardValueException extends RuntimeException {
}
