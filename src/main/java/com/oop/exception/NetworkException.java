package com.oop.exception;

public class NetworkException extends Exception {
    public NetworkException(String msg) {
        super(msg);
    }

    public NetworkException(Throwable cause) {
        super(cause);
    }
}
