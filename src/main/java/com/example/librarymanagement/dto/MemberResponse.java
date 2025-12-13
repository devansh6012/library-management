package com.example.librarymanagement.dto;

import com.example.librarymanagement.Member;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class MemberResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private LocalDateTime membershipDate;
    private boolean isActive;
    private int borrowedBooksCount;
    private List<BookResponse> borrowedBooks;
    private LocalDateTime createdAt;

    // Constructors
    public MemberResponse() {}

    // Static factory method
    public static MemberResponse from(Member member) {
        MemberResponse response = new MemberResponse();
        response.id = member.getId();
        response.name = member.getName();
        response.email = member.getEmail();
        response.phone = member.getPhone();
        response.membershipDate = member.getMembershipDate();
        response.isActive = member.isActive();
        response.createdAt = member.getCreatedAt();
        response.borrowedBooksCount = member.getBorrowedBooks().size();
        response.borrowedBooks = member.getBorrowedBooks().stream()
                .map(BookResponse::from)
                .collect(Collectors.toList());
        return response;
    }

    // Static factory method without borrowed books (to avoid lazy loading issues)
    public static MemberResponse fromWithoutBooks(Member member) {
        MemberResponse response = new MemberResponse();
        response.id = member.getId();
        response.name = member.getName();
        response.email = member.getEmail();
        response.phone = member.getPhone();
        response.membershipDate = member.getMembershipDate();
        response.isActive = member.isActive();
        response.createdAt = member.getCreatedAt();
        response.borrowedBooksCount = member.getBorrowedBooks().size();
        return response;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public LocalDateTime getMembershipDate() { return membershipDate; }
    public void setMembershipDate(LocalDateTime membershipDate) { this.membershipDate = membershipDate; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public int getBorrowedBooksCount() { return borrowedBooksCount; }
    public void setBorrowedBooksCount(int borrowedBooksCount) { this.borrowedBooksCount = borrowedBooksCount; }

    public List<BookResponse> getBorrowedBooks() { return borrowedBooks; }
    public void setBorrowedBooks(List<BookResponse> borrowedBooks) { this.borrowedBooks = borrowedBooks; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}