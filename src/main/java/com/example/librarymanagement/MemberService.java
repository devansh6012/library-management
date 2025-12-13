package com.example.librarymanagement;

import com.example.librarymanagement.dto.*;
import com.example.librarymanagement.exception.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;

    public MemberService(MemberRepository memberRepository, BookRepository bookRepository) {
        this.memberRepository = memberRepository;
        this.bookRepository = bookRepository;
    }

    public Page<MemberResponse> getAllMembers(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return memberRepository.findAll(pageable)
                .map(MemberResponse::fromWithoutBooks);
    }

    public MemberResponse addMember(CreateMemberRequest request) {
        // Check if email already exists
        if (memberRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new MemberAlreadyExistsException("Member with email " + request.getEmail() + " already exists");
        }

        Member member = new Member(request.getName(), request.getEmail(), request.getPhone());
        Member savedMember = memberRepository.save(member);
        System.out.println("✅ New member added: " + request.getName());
        return MemberResponse.from(savedMember);
    }

    public Optional<MemberResponse> findMemberById(Long id) {
        return memberRepository.findById(id)
                .map(MemberResponse::from);
    }

    public Optional<MemberResponse> findMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .map(MemberResponse::from);
    }

    public List<MemberResponse> searchMembers(String name) {
        return memberRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(MemberResponse::fromWithoutBooks)
                .collect(Collectors.toList());
    }

    public List<MemberResponse> getActiveMembers() {
        return memberRepository.findByIsActive(true)
                .stream()
                .map(MemberResponse::fromWithoutBooks)
                .collect(Collectors.toList());
    }

    public void deactivateMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("Member with ID " + id + " not found"));

        // Check if member has borrowed books
        if (!member.getBorrowedBooks().isEmpty()) {
            throw new MemberHasBorrowedBooksException("Cannot deactivate member with borrowed books");
        }

        member.setActive(false);
        memberRepository.save(member);
        System.out.println("🔒 Member deactivated: " + member.getName());
    }

    public List<MemberResponse> getMembersWithOverdueBooks() {
        return memberRepository.findMembersWithOverdueBooks()
                .stream()
                .map(MemberResponse::fromWithoutBooks)
                .collect(Collectors.toList());
    }

    public List<BookResponse> getMemberBorrowedBooks(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member with ID " + memberId + " not found"));

        return member.getBorrowedBooks()
                .stream()
                .map(BookResponse::from)
                .collect(Collectors.toList());
    }

    public long getTotalMembersCount() {
        return memberRepository.count();
    }

    public long getActiveMembersCount() {
        return memberRepository.countByIsActive(true);
    }
}