package com.danielnery.barbearia.api.Exception;




public class ServicoJaExisteException extends RuntimeException{
        public ServicoJaExisteException(String message){
            super(message);
        }
}
