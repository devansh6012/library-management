package com.example.librarymanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class BorrowBookRequest {

    @NotBlank(message = "Member name is required")
    @Size(min = 2, max = 50, message = "Member name must be between 2 and 50 characters")
    private String memberName;

//    Constructors
    public BorrowBookRequest() {}
    public BorrowBookRequest(String memberName) {
        this.memberName = memberName;
    }

//    Getters and Setters
    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }
}
