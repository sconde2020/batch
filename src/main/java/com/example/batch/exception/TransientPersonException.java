package com.example.batch.exception;

public class TransientPersonException extends RuntimeException {

    public TransientPersonException(String message) {
        super(message);
    }
}

