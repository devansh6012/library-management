package com.example.librarymanagement.security;

public enum Role {
    ROLE_ADMIN,      // Can do everything
    ROLE_LIBRARIAN,  // Can manage books and borrowing
    ROLE_MEMBER      // Can only borrow/return books
}