package com.james.base.exception;

public class XueChengPlusException extends RuntimeException{
    public XueChengPlusException() {
    }

    public XueChengPlusException(String message) {
        super(message);

    }
    public  static void cast(String message){
            throw new XueChengPlusException(message);
    }
}
