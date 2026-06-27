package com.danielnery.barbearia.api.Exception;


public class BarbeiroNaoEncontrado extends RuntimeException{
    public BarbeiroNaoEncontrado(String message){
        super(message);
    }
}
