package com.example.librarymanagement.exception;

public class MemberHasBorrowedBooksException extends RuntimeException {
    public MemberHasBorrowedBooksException(String message) {
        super(message);
    }
}