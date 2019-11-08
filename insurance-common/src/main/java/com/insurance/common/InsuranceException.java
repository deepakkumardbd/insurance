package com.insurance.common;

import net.corda.core.serialization.CordaSerializable;

@CordaSerializable
public class InsuranceException extends RuntimeException{

    public InsuranceException(){
    }

    public InsuranceException(String msg){
        super(msg);
    }

    public InsuranceException(String msg, Throwable cause){
        super (msg, cause);
    }

    public InsuranceException(Throwable cause){
        super(cause);
    }
}
