package com.example.librarymanagement.dto;

import com.example.librarymanagement.Book;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookResponse {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private boolean available;
    private String borrowedByMemberName;
    private Long borrowedByMemberId;
    private LocalDateTime borrowedDate;
    private LocalDateTime dueDate;
    private boolean overdue;
    private LocalDateTime createdAt;

    // Constructors
    public BookResponse() {}

    // Static factory method
    public static BookResponse from(Book book) {
        BookResponse response = new BookResponse();
        response.id = book.getId();
        response.title = book.getTitle();
        response.author = book.getAuthor();
        response.isbn = book.getIsbn();
        response.available = book.isAvailable();
        response.borrowedDate = book.getBorrowedDate();
        response.dueDate = book.getDueDate();
        response.overdue = book.isOverdue();
        response.createdAt = book.getCreatedAt();

        if (book.getBorrowedBy() != null) {
            response.borrowedByMemberName = book.getBorrowedBy().getName();
            response.borrowedByMemberId = book.getBorrowedBy().getId();
        }

        return response;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public String getBorrowedByMemberName() { return borrowedByMemberName; }
    public void setBorrowedByMemberName(String borrowedByMemberName) { this.borrowedByMemberName = borrowedByMemberName; }

    public Long getBorrowedByMemberId() { return borrowedByMemberId; }
    public void setBorrowedByMemberId(Long borrowedByMemberId) { this.borrowedByMemberId = borrowedByMemberId; }

    public LocalDateTime getBorrowedDate() { return borrowedDate; }
    public void setBorrowedDate(LocalDateTime borrowedDate) { this.borrowedDate = borrowedDate; }

    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }

    public boolean isOverdue() { return overdue; }
    public void setOverdue(boolean overdue) { this.overdue = overdue; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}