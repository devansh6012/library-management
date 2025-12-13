package com.example.librarymanagement;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // Existing methods...
    List<Book> findByAuthor(String author);
    List<Book> findByTitleContaining(String title);
    List<Book> findByIsAvailable(boolean available);

    // Pagination support
    Page<Book> findByAuthor(String author, Pageable pageable);
    Page<Book> findByIsAvailable(boolean available, Pageable pageable);

    // Relationship-based queries
    List<Book> findByBorrowedByIsNull();  // Available books
    List<Book> findByBorrowedByIsNotNull();  // Borrowed books
    List<Book> findByBorrowedBy(Member member);
    List<Book> findByBorrowedByName(String memberName);

    // Date-based queries
    List<Book> findByDueDateBefore(LocalDateTime date);  // Overdue books
    List<Book> findByBorrowedDateBetween(LocalDateTime start, LocalDateTime end);

    // Custom JPQL queries
    @Query("SELECT b FROM Book b WHERE b.title LIKE %:keyword% OR b.author LIKE %:keyword% OR b.isbn LIKE %:keyword%")
    Page<Book> searchBooks(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.borrowedBy.name = :memberName AND b.isAvailable = false")
    List<Book> findCurrentlyBorrowedBooksByMemberName(@Param("memberName") String memberName);

    // Complex aggregation query
    @Query("SELECT b.author, COUNT(b) FROM Book b GROUP BY b.author ORDER BY COUNT(b) DESC")
    List<Object[]> findAuthorBookCounts();

    // Books borrowed by active members
    @Query("SELECT b FROM Book b WHERE b.borrowedBy IS NOT NULL AND b.borrowedBy.isActive = true")
    List<Book> findBooksBorrowedByActiveMembers();

    // Native SQL for complex reporting
    @Query(value = """
        SELECT b.*, m.name as member_name, 
               DATEDIFF(CURRENT_DATE, b.borrowed_date) as days_borrowed
        FROM books b 
        JOIN members m ON b.borrowed_by_member_id = m.id 
        WHERE b.is_available = false 
        ORDER BY days_borrowed DESC
        """, nativeQuery = true)
    List<Object[]> findBorrowedBooksWithDuration();

    // Count methods
    long countByAuthor(String author);
    long countByIsAvailable(boolean available);
    long countByBorrowedByIsNotNull();  // Total borrowed books

    @Query("SELECT COUNT(b) FROM Book b WHERE b.dueDate < CURRENT_TIMESTAMP AND b.isAvailable = false")
    long countOverdueBooks();
}