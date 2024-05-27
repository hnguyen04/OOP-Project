package com.oop.exception;

public class InvalidSearchQueryException extends Exception {
    public InvalidSearchQueryException(String message) {
        super(message);
    }
}