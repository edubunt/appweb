package com.application.appweb.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id){
        super("Membro com ID "+id+" não encontrado");
    }
    public UserNotFoundException(String message){
        super(message);
    }

}
