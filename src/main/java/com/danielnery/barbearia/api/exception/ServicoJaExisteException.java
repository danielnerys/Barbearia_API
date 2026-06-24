package com.danielnery.barbearia.api.exception;




public class ServicoJaExisteException extends RuntimeException{
        public ServicoJaExisteException(String message){
            super(message);
        }
}
