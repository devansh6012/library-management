package com.example.librarymanagement;

import com.example.librarymanagement.dto.CreateBookRequest;
import com.example.librarymanagement.dto.BorrowBookRequest;
import com.example.librarymanagement.dto.BookResponse;
import com.example.librarymanagement.security.Role;
import com.example.librarymanagement.security.User;
import com.example.librarymanagement.security.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class LibraryManagementApplication {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Constructor injection for UserRepository and PasswordEncoder
    public LibraryManagementApplication(UserRepository userRepository,
                                        PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public static void main(String[] args) {
        System.out.println("🚀 Starting Library Management System...");
        SpringApplication.run(LibraryManagementApplication.class, args);
    }

    // Create default users on application startup
    @PostConstruct
    public void createDefaultUsers() {
        if (userRepository.count() == 0) {
            System.out.println("🔐 Creating default users...");

            // Create Admin user
            User admin = new User("admin", passwordEncoder.encode("admin123"), "admin@library.com");
            admin.addRole(Role.ROLE_ADMIN);
            userRepository.save(admin);

            // Create Librarian user
            User librarian = new User("librarian", passwordEncoder.encode("librarian123"), "librarian@library.com");
            librarian.addRole(Role.ROLE_LIBRARIAN);
            userRepository.save(librarian);

            // Create Member user
            User member = new User("member", passwordEncoder.encode("member123"), "member@library.com");
            member.addRole(Role.ROLE_MEMBER);
            userRepository.save(member);

            System.out.println("✅ Default users created!");
            System.out.println("   Admin: admin / admin123");
            System.out.println("   Librarian: librarian / librarian123");
            System.out.println("   Member: member / member123");
        }
    }
}