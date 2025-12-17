package com.example.librarymanagement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member testMember;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        memberRepository.deleteAll();

        testMember = new Member("Test Member", "test@test.com", "123");
        testMember = memberRepository.save(testMember);
    }

    @Test
    void shouldSaveAndFindBook() {
        // Arrange
        Book book = new Book("Test Book", "Test Author");

        // Act
        Book savedBook = bookRepository.save(book);
        Optional<Book> found = bookRepository.findById(savedBook.getId());

        // Assert
        assertTrue(found.isPresent());
        assertEquals("Test Book", found.get().getTitle());
        assertEquals("Test Author", found.get().getAuthor());
    }

    @Test
    void shouldFindBooksByAuthor() {
        // Arrange
        bookRepository.save(new Book("Book 1", "Author A"));
        bookRepository.save(new Book("Book 2", "Author A"));
        bookRepository.save(new Book("Book 3", "Author B"));

        // Act
        List<Book> booksFromAuthorA = bookRepository.findByAuthor("Author A");

        // Assert
        assertEquals(2, booksFromAuthorA.size());
        assertTrue(booksFromAuthorA.stream()
                .allMatch(book -> book.getAuthor().equals("Author A")));
    }

    @Test
    void shouldFindBooksByTitleContaining() {
        // Arrange
        bookRepository.save(new Book("Spring Boot Guide", "Author"));
        bookRepository.save(new Book("Spring Framework", "Author"));
        bookRepository.save(new Book("Java Basics", "Author"));

        // Act
        List<Book> springBooks = bookRepository.findByTitleContaining("Spring");

        // Assert
        assertEquals(2, springBooks.size());
        assertTrue(springBooks.stream()
                .allMatch(book -> book.getTitle().contains("Spring")));
    }

    @Test
    void shouldFindAvailableBooks() {
        // Arrange
        Book availableBook = new Book("Available Book", "Author");
        Book borrowedBook = new Book("Borrowed Book", "Author");
        borrowedBook.borrowBook(testMember, 14);

        bookRepository.save(availableBook);
        bookRepository.save(borrowedBook);

        // Act
        List<Book> availableBooks = bookRepository.findByIsAvailable(true);

        // Assert
        assertEquals(1, availableBooks.size());
        assertEquals("Available Book", availableBooks.get(0).getTitle());
    }

    @Test
    void shouldFindBooksBorrowedByMember() {
        // Arrange
        Book book1 = new Book("Book 1", "Author");
        book1.borrowBook(testMember, 14);

        Book book2 = new Book("Book 2", "Author");
        book2.borrowBook(testMember, 14);

        Book book3 = new Book("Book 3", "Author");

        bookRepository.save(book1);
        bookRepository.save(book2);
        bookRepository.save(book3);

        // Act
        List<Book> borrowedBooks = bookRepository.findByBorrowedBy(testMember);

        // Assert
        assertEquals(2, borrowedBooks.size());
    }

    @Test
    void shouldFindOverdueBooks() {
        // Arrange
        Book overdueBook = new Book("Overdue Book", "Author");
        overdueBook.borrowBook(testMember, 14);
        overdueBook.setDueDate(LocalDateTime.now().minusDays(1));  // Set due date in past

        Book notOverdueBook = new Book("Not Overdue Book", "Author");
        notOverdueBook.borrowBook(testMember, 14);
        notOverdueBook.setDueDate(LocalDateTime.now().plusDays(7));

        bookRepository.save(overdueBook);
        bookRepository.save(notOverdueBook);

        // Act
        List<Book> overdueBooks = bookRepository.findByDueDateBefore(LocalDateTime.now());

        // Assert
        assertEquals(1, overdueBooks.size());
        assertEquals("Overdue Book", overdueBooks.get(0).getTitle());
    }

    @Test
    void shouldCountBooksByAuthor() {
        // Arrange
        bookRepository.save(new Book("Book 1", "Author A"));
        bookRepository.save(new Book("Book 2", "Author A"));
        bookRepository.save(new Book("Book 3", "Author A"));
        bookRepository.save(new Book("Book 4", "Author B"));

        // Act
        long count = bookRepository.countByAuthor("Author A");

        // Assert
        assertEquals(3, count);
    }

    @Test
    void shouldDeleteBook() {
        // Arrange
        Book book = bookRepository.save(new Book("To Delete", "Author"));
        Long bookId = book.getId();

        // Act
        bookRepository.deleteById(bookId);
        Optional<Book> found = bookRepository.findById(bookId);

        // Assert
        assertFalse(found.isPresent());
    }
}