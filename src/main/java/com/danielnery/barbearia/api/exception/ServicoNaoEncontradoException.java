package com.danielnery.barbearia.api.exception;

public class ServicoNaoEncontradoException extends RuntimeException{
        public ServicoNaoEncontradoException(String message){
            super(message);
    }
}
