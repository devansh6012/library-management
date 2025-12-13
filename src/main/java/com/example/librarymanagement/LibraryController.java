package com.example.librarymanagement;

import com.example.librarymanagement.dto.ApiResponse;
import com.example.librarymanagement.dto.BookResponse;
import com.example.librarymanagement.dto.BorrowBookRequest;
import com.example.librarymanagement.dto.CreateBookRequest;
import com.example.librarymanagement.exception.BookNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController  // Handles web requests and returns JSON
@RequestMapping("/api/v1/library")
@CrossOrigin(origins = "*") // Allow cross-origin requests
public class LibraryController {

    private final LibraryService libraryService;

    // Constructor injection
    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
        System.out.println("🌐 LibraryController created!");
    }

    @GetMapping("/info")
    public ResponseEntity<ApiResponse<String>> getLibraryInfo() {
        String info = libraryService.getLibraryInfo();
        return ResponseEntity.ok(ApiResponse.success("Library information retrieved", info));
    }

    @GetMapping("/books")
    public ResponseEntity<ApiResponse<List<BookResponse>>> getAllBooks() {
        List<BookResponse> books = libraryService.getAllBooks();
        return ResponseEntity.ok(ApiResponse.success("Books retrieved successfully", books));
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<ApiResponse<BookResponse>> getBookById(@PathVariable Long id) {
        BookResponse book = libraryService.findBookById(id)
                .orElseThrow(() -> new BookNotFoundException("Book with ID " + id + " not found"));
        return ResponseEntity.ok(ApiResponse.success("Book retrieved successfully", book));
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteBook(@PathVariable Long id) {
        libraryService.deleteBook(id);
        return ResponseEntity.ok(ApiResponse.success("Book deleted successfully", null));
    }

    @PostMapping("/books")
    public ResponseEntity<ApiResponse<BookResponse>> addBook(@RequestBody @Valid CreateBookRequest request) {
        BookResponse book = libraryService.addBook(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Book created successfully", book));
    }

    @PostMapping("/books/{id}/borrow")
    public ResponseEntity<ApiResponse<String>> borrowBook(
            @PathVariable Long id,
            @RequestBody @Valid BorrowBookRequest request) {

        boolean success = libraryService.borrowBook(id, request);
        if (success) {
            return ResponseEntity.ok(ApiResponse.success("Book borrowed successfully",
                    "Book has been borrowed by " + request.getMemberName()));
        } else {
            throw new BookNotFoundException("Book with ID " + id + " not found");
        }
    }

    @PostMapping("/books/{id}/return")
    public ResponseEntity<ApiResponse<String>> returnBook(
            @PathVariable Long id,
            @RequestBody @Valid BorrowBookRequest request) {

        boolean success = libraryService.returnBook(id, request);
        if (success) {
            return ResponseEntity.ok(ApiResponse.success("Book returned successfully",
                    "Book has been returned by " + request.getMemberName()));
        } else {
            throw new BookNotFoundException("Book with ID " + id + " not found");
        }
    }
}