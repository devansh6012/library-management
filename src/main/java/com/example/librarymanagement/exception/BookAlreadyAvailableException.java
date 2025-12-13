package com.example.librarymanagement.exception;

public class BookAlreadyAvailableException extends RuntimeException {
    public BookAlreadyAvailableException(String message) {
        super(message);
    }
}