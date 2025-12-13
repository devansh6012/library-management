package com.example.librarymanagement;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "author", nullable = false, length = 50)
    private String author;

    @Column(name = "isbn", unique = true, length = 20)
    private String isbn;

    @Column(name = "is_available", nullable = false)
    private boolean isAvailable;

    // Many-to-One relationship: Many books can be borrowed by one member
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrowed_by_member_id")  // Foreign key column
    private Member borrowedBy;

    @Column(name = "borrowed_date")
    private LocalDateTime borrowedDate;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Book() {}

    public Book(String title, String author) {
        this.title = title;
        this.author = author;
        this.isAvailable = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Book(String title, String author, String isbn) {
        this(title, author);
        this.isbn = isbn;
    }

    // JPA lifecycle methods
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Business methods
    public void borrowBook(Member member, int loanDurationDays) {
        this.isAvailable = false;
        this.borrowedBy = member;
        this.borrowedDate = LocalDateTime.now();
        this.dueDate = borrowedDate.plusDays(loanDurationDays);
        this.updatedAt = LocalDateTime.now();
    }

    public void returnBook() {
        this.isAvailable = true;
        this.borrowedBy = null;
        this.borrowedDate = null;
        this.dueDate = null;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isOverdue() {
        return dueDate != null && LocalDateTime.now().isAfter(dueDate);
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) {
        this.title = title;
        this.updatedAt = LocalDateTime.now();
    }

    public String getAuthor() { return author; }
    public void setAuthor(String author) {
        this.author = author;
        this.updatedAt = LocalDateTime.now();
    }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) {
        this.isbn = isbn;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) {
        isAvailable = available;
        this.updatedAt = LocalDateTime.now();
    }

    public Member getBorrowedBy() { return borrowedBy; }
    public void setBorrowedBy(Member borrowedBy) { this.borrowedBy = borrowedBy; }

    public LocalDateTime getBorrowedDate() { return borrowedDate; }
    public void setBorrowedDate(LocalDateTime borrowedDate) { this.borrowedDate = borrowedDate; }

    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Book{id=" + id + ", title='" + title + "', author='" + author +
                "', available=" + isAvailable +
                ", borrowedBy=" + (borrowedBy != null ? borrowedBy.getName() : "none") + "}";
    }
}