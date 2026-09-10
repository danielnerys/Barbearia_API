package com.danielnery.barbearia.api.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> tratarValidacao(MethodArgumentNotValidException exception) {
        Map<String, String> erro = new HashMap<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            erro.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> tratarAccessDenied(AccessDeniedException exception) {
        Map<String, String> erro = new HashMap<>();
        erro.put("mensagem", "Você não tem permissão para executar esta ação.");

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(erro);
    }

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

    @ExceptionHandler(AgendamentoNaoEncontrado.class)
    public ResponseEntity<Map<String,String >> AgendamentoNaoEncontrado(RuntimeException exception){
        Map<String, String> erro = new HashMap<>();
        erro.put("mensagem", exception.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(UsuarioNaoEncontrado.class)
    public ResponseEntity<Map<String,String >> UsuarioNaoEncontrado(RuntimeException exception){
        Map<String, String> erro = new HashMap<>();
        erro.put("mensagem", exception.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }


    @ExceptionHandler(BarbeiroInativoException.class)
    public ResponseEntity<Map<String, String>> BarbeirInativoException(RuntimeException exception){
        Map<String, String> erro = new HashMap<>();

        erro.put("mensagem", exception.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(ServicoInativoException.class)
    public ResponseEntity<Map<String, String>> ServicoInativoException(RuntimeException exception){
        Map<String, String> erro = new HashMap<>();

        erro.put("mensagem", exception.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(HorarioIndisponivelException.class)
    public ResponseEntity<Map<String, String>> HorarioIndisponivelException(RuntimeException exception){
        Map<String, String> erro = new HashMap<>();

        erro.put("mensagem", exception.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(UsuarioJaExisteException.class)
    public ResponseEntity<Map<String, String>> UsuarioJaExisteExeption(RuntimeException exception){
        Map<String, String> erro = new HashMap<>();

        erro.put("mensagem", exception.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(OperacaoNaoPermitidaException.class)
    public ResponseEntity<Map<String, String>> OperacaoNaoPermitidaException(RuntimeException exception){
        Map<String, String> erro = new HashMap<>();

        erro.put("mensagem", exception.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(erro);
    }
}
