package com.example.librarymanagement;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    // Query method naming convention
    Optional<Member> findByEmail(String email);

    List<Member> findByNameContainingIgnoreCase(String name);

    List<Member> findByIsActive(boolean isActive);

    Page<Member> findByIsActive(boolean isActive, Pageable pageable);

    // Custom JPQL queries
    @Query("SELECT m FROM Member m WHERE SIZE(m.borrowedBooks) > :count")
    List<Member> findMembersWithMoreThanXBorrowedBooks(@Param("count") int count);

    @Query("SELECT m FROM Member m WHERE m.membershipDate BETWEEN :startDate AND :endDate")
    List<Member> findMembersByMembershipDateRange(@Param("startDate") LocalDateTime startDate,
                                                  @Param("endDate") LocalDateTime endDate);

    // Complex query with JOIN
    @Query("SELECT DISTINCT m FROM Member m JOIN m.borrowedBooks b WHERE b.author = :author")
    List<Member> findMembersByBorrowedBooksAuthor(@Param("author") String author);

    // Native SQL query for complex statistics
    @Query(value = """
        SELECT m.* FROM members m 
        WHERE m.id IN (
            SELECT b.borrowed_by_member_id 
            FROM books b 
            WHERE b.due_date < CURRENT_TIMESTAMP 
            AND b.borrowed_by_member_id IS NOT NULL
        )
        """, nativeQuery = true)
    List<Member> findMembersWithOverdueBooks();

    // Count queries
    long countByIsActive(boolean isActive);

    @Query("SELECT COUNT(DISTINCT m) FROM Member m JOIN m.borrowedBooks b WHERE b.isAvailable = false")
    long countMembersWithBorrowedBooks();
}