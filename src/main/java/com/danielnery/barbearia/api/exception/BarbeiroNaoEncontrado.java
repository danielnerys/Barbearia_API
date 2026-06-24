package com.danielnery.barbearia.api.exception;


public class BarbeiroNaoEncontrado extends RuntimeException{
    public BarbeiroNaoEncontrado(String message){
        super(message);
    }
}
