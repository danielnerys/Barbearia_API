package com.danielnery.barbearia.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ServicoJaExisteException.class)
    public ResponseEntity<Map<String, String>> tratarRunTimeException(RuntimeException exception) {
        Map<String, String> erro = new HashMap<>();
        erro.put("mensagem", exception.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }
    @ExceptionHandler(ServicoNaoEncontradoException.class)
    public ResponseEntity<Map<String, String>> ServicoNaoEncontradoException(RuntimeException exception) {
        Map<String, String> erro = new HashMap<>();
        erro.put("mensagem", exception.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND
        ).body(erro);
    }

    @ExceptionHandler(BarbeiroNaoEncontrado.class)
    public ResponseEntity<Map<String, String>> BarbeiroNaoEncontrado(RuntimeException exception) {
        Map<String, String> erro = new HashMap<>();
        erro.put("mensagem", exception.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND
        ).body(erro);
    }
}
