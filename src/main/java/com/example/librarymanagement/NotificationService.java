package com.example.librarymanagement;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component // Generic Spring component
@Scope("singleton") // Default scope - one instance for entire application
public class NotificationService {

    @PostConstruct
    public void init() {
        System.out.println("📧 NotificationService initialized!");
    }

    public void sendBookBorrowedNotification(String memberName, Book book) {
        System.out.println("📧 NOTIFICATION: " + memberName + " borrowed '" + book.getTitle() + "'");
    }

    public void sendBookReturnedNotification(String memberName, Book book) {
        System.out.println("📧 NOTIFICATION: " + memberName + " returned '" + book.getTitle() + "'");
    }

    public void sendOverdueNotification(String memberName, Book book) {
        System.out.println("⚠️ OVERDUE NOTICE: " + memberName + " - '" + book.getTitle() + "' is overdue!");
    }

}
