package com.template.insurance.exception;


import net.corda.core.serialization.CordaSerializable;

@CordaSerializable
public class ServerException extends RuntimeException{
    public ServerException(String msg){
        super(msg);
    }
}
