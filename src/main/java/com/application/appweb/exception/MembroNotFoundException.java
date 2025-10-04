package com.application.appweb.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MembroNotFoundException extends RuntimeException{
    public MembroNotFoundException(Long id){
        super("Membro com ID "+id+" não encontrado");
    }
    public MembroNotFoundException(String message){
        super(message);
    }
    
}
