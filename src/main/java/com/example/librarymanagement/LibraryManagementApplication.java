package com.example.librarymanagement;

import com.example.librarymanagement.dto.CreateBookRequest;
import com.example.librarymanagement.dto.BorrowBookRequest;
import com.example.librarymanagement.dto.BookResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication // This includes @Configuration + @ComponentScan + @EnableAutoConfiguration
public class LibraryManagementApplication {
    public static void main(String[] args) {
        System.out.println("🚀 Starting Library Management System...");

        // Start Spring Boot application
        ApplicationContext context = SpringApplication.run(LibraryManagementApplication.class, args);

        // Demo: Get a bean from Spring container and use it
        LibraryService libraryService = context.getBean(LibraryService.class);

        System.out.println("\n" + "=".repeat(50));
        System.out.println("📚 LIBRARY MANAGEMENT SYSTEM DEMO (Phase 3 - DTOs)");
        System.out.println("=".repeat(50));

        try {
            // Demo operations using new DTO-based methods
            System.out.println("\n1. Displaying all books:");
            libraryService.getAllBooks().forEach(book ->
                    System.out.println("📖 " + book.getId() + ": " + book.getTitle() +
                            " by " + book.getAuthor() + " (Available: " + book.isAvailable() + ")")
            );

            System.out.println("\n2. Adding a new book:");
            CreateBookRequest newBookRequest = new CreateBookRequest("Effective Java", "Joshua Bloch");
            BookResponse newBook = libraryService.addBook(newBookRequest);
            System.out.println("📚 Added: " + newBook.getTitle() + " (ID: " + newBook.getId() + ")");

            System.out.println("\n3. Borrowing a book:");
            BorrowBookRequest borrowRequest = new BorrowBookRequest("John Doe");
            boolean borrowSuccess = libraryService.borrowBook(1L, borrowRequest);
            System.out.println("📖 Borrow result: " + (borrowSuccess ? "Success" : "Failed"));

            System.out.println("\n4. Trying to borrow the same book again:");
            BorrowBookRequest borrowRequest2 = new BorrowBookRequest("Jane Smith");
            try {
                libraryService.borrowBook(1L, borrowRequest2);
            } catch (Exception e) {
                System.out.println("❌ Expected error: " + e.getMessage());
            }

            System.out.println("\n5. Returning the book:");
            BorrowBookRequest returnRequest = new BorrowBookRequest("John Doe");
            boolean returnSuccess = libraryService.returnBook(1L, returnRequest);
            System.out.println("📚 Return result: " + (returnSuccess ? "Success" : "Failed"));

            System.out.println("\n6. Current library status:");
            libraryService.getAllBooks().forEach(book ->
                    System.out.println("📖 " + book.getId() + ": " + book.getTitle() +
                            " by " + book.getAuthor() + " (Available: " + book.isAvailable() + ")")
            );

            System.out.println("\n7. Testing error handling - Get non-existent book:");
            try {
                libraryService.findBookById(999L).orElseThrow(() ->
                        new RuntimeException("Book with ID 999 not found"));
            } catch (Exception e) {
                System.out.println("❌ Expected error: " + e.getMessage());
            }

        } catch (Exception e) {
            System.out.println("❌ Unexpected error during demo: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.println("✅ Demo completed! Check the REST API endpoints:");
        System.out.println("📍 GET  http://localhost:8080/api/v1/library/books");
        System.out.println("📍 GET  http://localhost:8080/api/v1/library/info");
        System.out.println("📍 POST http://localhost:8080/api/v1/library/books");
        System.out.println("📍 Try the test-enhanced-api.http file for full API testing!");
        System.out.println("=".repeat(60));
    }

    
}