package com.example.librarymanagement;

import com.example.librarymanagement.dto.BookResponse;
import com.example.librarymanagement.dto.BorrowBookRequest;
import com.example.librarymanagement.dto.CreateBookRequest;
import com.example.librarymanagement.exception.BookAlreadyAvailableException;
import com.example.librarymanagement.exception.BookNotAvailableException;
import com.example.librarymanagement.exception.BookNotFoundException;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service  // Business logic layer
@Transactional // Database transactions managed automatically
public class LibraryService {

    // Spring will automatically inject these dependencies
    private final BookRepository bookRepository; // Now a JPA repository
    private final NotificationService notificationService;
    private final LibraryConfigProperties config;
    private final MemberRepository memberRepository;
    @Value("${library.name}")
    private String libraryName;

    @Value("${library.max-books-per-user}")
    private int maxBooksPerUser;

    @Value("${library.late-fee-per-day}")
    private double lateFeePerDay;

    // Constructor injection (recommended way)
    public LibraryService(BookRepository bookRepository, NotificationService notificationService, LibraryConfigProperties config, MemberRepository memberRepository) {
        this.bookRepository = bookRepository;
        this.notificationService = notificationService;
        this.config = config;
        this.memberRepository = memberRepository;
        System.out.println("🏗️ LibraryService created with dependencies injected!");
    }

    @PostConstruct
    public void init() {
        System.out.println("📖 " + libraryName + " is ready to serve!");
        System.out.println("📋 Max books per user: " + maxBooksPerUser);
        System.out.println("💰 Late fee per day: $" + config.getLateFeePerDay());

        // Add some sample books if database is empty
        if (bookRepository.count() == 0) {
            System.out.println("🔄 Database is empty, adding sample books...");
            bookRepository.save(new Book("Spring in Action", "Craig Walls"));
            bookRepository.save(new Book("Java: The Complete Reference", "Herbert Schildt"));
            bookRepository.save(new Book("Clean Code", "Robert Martin"));
            System.out.println("✅ Sample books added to database!");
        }

        System.out.println("📊 Total books in database: " + bookRepository.count());
    }

    public List<BookResponse> getAllBooks() {
        System.out.println("📋 Fetching all books...");
        return bookRepository.findAll()
                .stream()
                .map(BookResponse::from)
                .collect(Collectors.toList());

    }

    public BookResponse addBook(CreateBookRequest request) {
        Book book = new Book(request.getTitle(), request.getAuthor());
        Book savedBook = bookRepository.save(book); // JPA automatically saves to database
        System.out.println("✅ New book added to library: " + request.getTitle());
        return BookResponse.from(savedBook);
    }

    public Optional<BookResponse> findBookById(Long id) {
        return bookRepository.findById(id)
                .map(BookResponse::from);
    }

    @Transactional
    public boolean borrowBook(Long bookId, BorrowBookRequest request) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book with ID " + bookId + " not found"));

        if (!book.isAvailable()) {
            throw new BookNotAvailableException("Book '" + book.getTitle() + "' is not available for borrowing");
        }

        // Find or create member
        Member member = memberRepository.findByEmail(request.getMemberName() + "@library.com") // Assuming email format
                .orElse(new Member(request.getMemberName(), request.getMemberName() + "@library.com", ""));

        if (member.getId() == null) {
            member = memberRepository.save(member);
        }

        // Use the new business method
        book.borrowBook(member, 14); // 14 days loan period
        member.addBorrowedBook(book);

        bookRepository.save(book);
        memberRepository.save(member);

        notificationService.sendBookBorrowedNotification(request.getMemberName(), book);
        System.out.println("📖 Book borrowed with relationship tracking!");
        return true;
    }

    @Transactional
    public boolean returnBook(Long bookId, BorrowBookRequest request) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book with ID " + bookId + " not found"));

        if (book.isAvailable()) {
            throw new BookAlreadyAvailableException("Book '" + book.getTitle() + "' was not borrowed");
        }

        Member member = book.getBorrowedBy();
        if (member != null) {
            member.removeBorrowedBook(book);
            memberRepository.save(member);
        }

        book.returnBook();
        bookRepository.save(book);

        notificationService.sendBookReturnedNotification(request.getMemberName(), book);
        System.out.println("📚 Book returned with relationship tracking!");
        return true;
    }

    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) { // JPA method
            throw new BookNotFoundException("Book with ID " + id + " not found");
        }
        bookRepository.deleteById(id); // JPA method
        System.out.println("🗑️ Book deleted successfully!");
    }

    // New methods using custom repository queries
    public Page<BookResponse> searchBooks(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.searchBooks(keyword, pageable)
                .map(BookResponse::from);
    }

    public List<BookResponse> findBooksByAuthor(String author) {
        return bookRepository.findByAuthor(author)
                .stream()
                .map(BookResponse::from)
                .collect(Collectors.toList());
    }

    public List<BookResponse> findAvailableBooks() {
        return bookRepository.findByIsAvailable(true)
                .stream()
                .map(BookResponse::from)
                .collect(Collectors.toList());
    }

    public String getLibraryInfo() {
        long totalBooks = bookRepository.count();
        long availableBooks = bookRepository.countByIsAvailable(true);

        return String.format("Welcome to %s! We have %d books total, %d available. " +
                        "You can borrow up to %d books. Late fee: $%.2f per day.",
                config.getName(), totalBooks, availableBooks,
                config.getMaxBooksPerUser(), config.getLateFeePerDay());
    }
}