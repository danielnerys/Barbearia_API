package com.danielnery.barbearia.api.Exception;

public class ServicoNaoEncontradoException extends RuntimeException{
        public ServicoNaoEncontradoException(String message){
            super(message);
    }
}
