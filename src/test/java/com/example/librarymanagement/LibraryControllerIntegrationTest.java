package com.example.librarymanagement;

import com.example.librarymanagement.dto.CreateBookRequest;
import com.example.librarymanagement.security.JwtUtil;
import com.example.librarymanagement.security.Role;
import com.example.librarymanagement.security.User;
import com.example.librarymanagement.security.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class LibraryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    private String adminToken;
    private String memberToken;

    @BeforeEach
    void setUp() {
        // Clean database
        bookRepository.deleteAll();
        userRepository.deleteAll();

        // Create test users
        User admin = new User("testadmin", passwordEncoder.encode("password"), "admin@test.com");
        admin.addRole(Role.ROLE_ADMIN);
        userRepository.save(admin);

        User member = new User("testmember", passwordEncoder.encode("password"), "member@test.com");
        member.addRole(Role.ROLE_MEMBER);
        userRepository.save(member);

        // Generate tokens
        UserDetails adminDetails = userDetailsService.loadUserByUsername("testadmin");
        adminToken = jwtUtil.generateToken(adminDetails);

        UserDetails memberDetails = userDetailsService.loadUserByUsername("testmember");
        memberToken = jwtUtil.generateToken(memberDetails);

        // Add test books
        bookRepository.save(new Book("Spring Boot in Action", "Craig Walls"));
        bookRepository.save(new Book("Clean Code", "Robert Martin"));
    }

    @Test
    void shouldGetAllBooksWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/library/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].title", notNullValue()));
    }

    @Test
    void shouldGetBookByIdWithoutAuthentication() throws Exception {
        Book book = bookRepository.findAll().get(0);

        mockMvc.perform(get("/api/v1/library/books/" + book.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.title", is(book.getTitle())))
                .andExpect(jsonPath("$.data.author", is(book.getAuthor())));
    }

    @Test
    void shouldReturn404WhenBookNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/library/books/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("not found")));
    }

    @Test
    void shouldAddBookWithAdminToken() throws Exception {
        CreateBookRequest request = new CreateBookRequest("New Book", "New Author");

        mockMvc.perform(post("/api/v1/library/books")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.title", is("New Book")))
                .andExpect(jsonPath("$.data.author", is("New Author")))
                .andExpect(jsonPath("$.data.available", is(true)));
    }

    @Test
    void shouldReturn401WhenAddingBookWithoutToken() throws Exception {
        CreateBookRequest request = new CreateBookRequest("New Book", "New Author");

        mockMvc.perform(post("/api/v1/library/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Unauthorized")));
    }

    @Test
    void shouldReturn403WhenAddingBookWithMemberToken() throws Exception {
        CreateBookRequest request = new CreateBookRequest("New Book", "New Author");

        mockMvc.perform(post("/api/v1/library/books")
                        .header("Authorization", "Bearer " + memberToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Access Denied")));
    }

    @Test
    void shouldDeleteBookWithAdminToken() throws Exception {
        Book book = bookRepository.findAll().get(0);

        mockMvc.perform(delete("/api/v1/library/books/" + book.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", containsString("deleted")));
    }

    @Test
    void shouldReturn400WhenAddingBookWithInvalidData() throws Exception {
        CreateBookRequest request = new CreateBookRequest("", "");  // Invalid - blank fields

        mockMvc.perform(post("/api/v1/library/books")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Validation failed")));
    }

    @Test
    void shouldGetLibraryInfo() throws Exception {
        mockMvc.perform(get("/api/v1/library/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", containsString("Test Library")))
                .andExpect(jsonPath("$.data", containsString("books total")));
    }
}