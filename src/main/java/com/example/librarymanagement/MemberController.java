package com.example.librarymanagement;

import com.example.librarymanagement.dto.*;
import com.example.librarymanagement.exception.MemberNotFoundException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/members")
@CrossOrigin(origins = "*")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<MemberResponse>>> getAllMembers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Page<MemberResponse> members = memberService.getAllMembers(page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success("Members retrieved successfully", members));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MemberResponse>> getMemberById(@PathVariable Long id) {
        MemberResponse member = memberService.findMemberById(id)
                .orElseThrow(() -> new MemberNotFoundException("Member with ID " + id + " not found"));
        return ResponseEntity.ok(ApiResponse.success("Member retrieved successfully", member));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<MemberResponse>> getMemberByEmail(@PathVariable String email) {
        MemberResponse member = memberService.findMemberByEmail(email)
                .orElseThrow(() -> new MemberNotFoundException("Member with email " + email + " not found"));
        return ResponseEntity.ok(ApiResponse.success("Member retrieved successfully", member));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MemberResponse>> addMember(@RequestBody @Valid CreateMemberRequest request) {
        MemberResponse member = memberService.addMember(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Member created successfully", member));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<MemberResponse>>> searchMembers(@RequestParam String name) {
        List<MemberResponse> members = memberService.searchMembers(name);
        return ResponseEntity.ok(ApiResponse.success("Search results", members));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<MemberResponse>>> getActiveMembers() {
        List<MemberResponse> members = memberService.getActiveMembers();
        return ResponseEntity.ok(ApiResponse.success("Active members retrieved", members));
    }

    @GetMapping("/overdue")
    public ResponseEntity<ApiResponse<List<MemberResponse>>> getMembersWithOverdueBooks() {
        List<MemberResponse> members = memberService.getMembersWithOverdueBooks();
        return ResponseEntity.ok(ApiResponse.success("Members with overdue books", members));
    }

    @GetMapping("/{id}/borrowed-books")
    public ResponseEntity<ApiResponse<List<BookResponse>>> getMemberBorrowedBooks(@PathVariable Long id) {
        List<BookResponse> books = memberService.getMemberBorrowedBooks(id);
        return ResponseEntity.ok(ApiResponse.success("Member's borrowed books", books));
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<String>> deactivateMember(@PathVariable Long id) {
        memberService.deactivateMember(id);
        return ResponseEntity.ok(ApiResponse.success("Member deactivated successfully", null));
    }

    @GetMapping("/stats/count")
    public ResponseEntity<ApiResponse<Object>> getMemberStats() {
        long totalMembers = memberService.getTotalMembersCount();
        long activeMembers = memberService.getActiveMembersCount();

        var stats = new Object() {
            public final long total = totalMembers;
            public final long active = activeMembers;
            public final long inactive = totalMembers - activeMembers;
        };

        return ResponseEntity.ok(ApiResponse.success("Member statistics", stats));
    }
}