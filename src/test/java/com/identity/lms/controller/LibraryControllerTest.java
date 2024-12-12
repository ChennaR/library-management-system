package com.identity.lms.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.identity.lms.common.ErrorResponse;
import com.identity.lms.domain.Book;
import com.identity.lms.repository.UserRepository;
import com.identity.lms.service.JwtAuthenticationService;
import com.identity.lms.service.LibraryService;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class LibraryControllerTest implements WithAssertions {

    private MockMvc mockMvc;
    @Autowired
    private LibraryService libraryService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private JwtAuthenticationService jwtAuthenticationService;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .alwaysDo(print())
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    private String createJwtToken(String user) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(userRepository.findById(user), "1234");
        return jwtAuthenticationService.createToken(usernamePasswordAuthenticationToken);
    }

    @Test
    void shouldAddBook() throws Exception {
        //given
        Book book = new Book("1234578", "James Bond", "Spielberg", 1990);
        String request = objectMapper.writeValueAsString(book);
        //when
        MvcResult mvcResult = mockMvc.perform(post("/api/v1/library/books")
                        .header("Authorization", "Bearer " + createJwtToken("admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();
        //then
        MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(201);
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(objectMapper.readValue(response.getContentAsString(), Book.class)).isEqualTo(book);
    }

    @Test
    void shouldRemoveBook() throws Exception {
        //given
        Book book = new Book("1234578", "James Bond", "Spielberg", 1990);
        libraryService.addBook(book);
        //when
        MvcResult mvcResult = mockMvc.perform(
                        delete("/api/v1/library/books/1234578")
                                .header("Authorization", "Bearer " + createJwtToken("admin")))
                .andReturn();
        //then
        MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldGetBookByIsbn() throws Exception {
        //given
        Map<String, Book> booksCache = (ConcurrentHashMap<String, Book>)
                Objects.requireNonNull(cacheManager.getCache("booksById")).getNativeCache();
        Book book1 = new Book("1234578", "James Bond", "Spielberg", 1990);
        Book book2 = new Book("9876542", "Micro strategy", "Ed Thomas", 2000);
        libraryService.addBook(book1);
        libraryService.addBook(book2);
        //when
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/library/books/isbn/9876542")
                        .header("Authorization", "Bearer " + createJwtToken("dev")))
                .andReturn();
        //then
        MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(objectMapper.readValue(response.getContentAsString(), Book.class)).isEqualTo(book2);
        assertThat(booksCache.get("9876542")).isEqualTo(book2);
    }

    @Test
    void shouldThrowExceptionWhenIsbnIsNotFound() throws Exception {
        //given
        Book book1 = new Book("1234578", "James Bond", "Spielberg", 1990);
        Book book2 = new Book("9876542", "Micro strategy", "Ed Thomas", 2000);
        libraryService.addBook(book1);
        libraryService.addBook(book2);
        //when
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/library/books/isbn/98765421")
                        .header("Authorization", "Bearer " + createJwtToken("dev")))
                .andReturn();
        //then
        MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(404);
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        ErrorResponse errorResponse = objectMapper.readValue(response.getContentAsString(), ErrorResponse.class);
        assertThat(errorResponse.getCode()).isEqualTo(4001);
        assertThat(errorResponse.getMessage()).isEqualTo("Book with ISBN 98765421 is not found");
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldGetBookByAuthor() throws Exception {
        //given
        Map<String, List<Book>> booksCache = (ConcurrentHashMap<String, List<Book>>)
                Objects.requireNonNull(cacheManager.getCache("booksByAuthor")).getNativeCache();
        List<Book> books = List.of(new Book("1234578", "James Bond", "Spielberg", 1990),
                new Book("9876542", "Micro strategy", "Spielberg", 2000));
        books.forEach(libraryService::addBook);
        //when
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/library/books/author/Spielberg")
                        .header("Authorization", "Bearer " + createJwtToken("dev")))
                .andReturn();
        //then
        MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        List<Book> result = objectMapper.readValue(response.getContentAsString(),
                new TypeReference<>() {
                });
        assertThat(result).containsAll(books);
        assertThat(booksCache.get("Spielberg")).isNotNull();
    }

    @Test
    void shouldBorrowBook() throws Exception {
        //given
        List<Book> books = List.of(new Book("1234578", "James Bond", "Spielberg", 1990),
                new Book("9876542", "Micro strategy", "Spielberg", 2000));
        books.forEach(libraryService::addBook);
        Book expected = new Book("9876542", "Micro strategy", "Spielberg", 2000)
                .availableCopies(0);
        //when
        MvcResult mvcResult = mockMvc.perform(patch("/api/v1/library/books/borrow/9876542")
                        .header("Authorization", "Bearer " + createJwtToken("dev")))
                .andReturn();
        //then
        MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        Book result = objectMapper.readValue(response.getContentAsString(), Book.class);
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void shouldThrowExceptionWhenThereAreNotEnoughCopiesToBorrowBook() throws Exception {
        //given
        List<Book> books = List.of(new Book("1234578", "James Bond", "Spielberg", 1990),
                new Book("9876542", "Micro strategy", "Spielberg", 2000).availableCopies(0));
        books.forEach(libraryService::addBook);
        //when
        MvcResult mvcResult = mockMvc.perform(patch("/api/v1/library/books/borrow/9876542")
                        .header("Authorization", "Bearer " + createJwtToken("dev")))
                .andReturn();
        //then
        MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(409);
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        ErrorResponse errorResponse = objectMapper.readValue(response.getContentAsString(), ErrorResponse.class);
        assertThat(errorResponse.getCode()).isEqualTo(4000);
        assertThat(errorResponse.getMessage()).isEqualTo("There are not enough copies to borrow with ISBN 9876542");
    }

    @Test
    void shouldReturnBook() throws Exception {
        //given
        List<Book> books = List.of(new Book("1234578", "James Bond", "Spielberg", 1990),
                new Book("9876542", "Micro strategy", "Spielberg", 2000));
        books.forEach(libraryService::addBook);
        libraryService.borrowBook("1234578");
        Book expected = new Book("1234578", "James Bond", "Spielberg", 1990)
                .availableCopies(1);
        //when
        MvcResult mvcResult = mockMvc.perform(patch("/api/v1/library/books/return/1234578")
                        .header("Authorization", "Bearer " + createJwtToken("dev")))
                .andReturn();
        //then
        MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        Book result = objectMapper.readValue(response.getContentAsString(), Book.class);
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void shouldThrowExceptionWhenBookIsNotValid() throws Exception {
        //given
        Book book = new Book("1234578", "James Bond", null, 1990);
        //when
        MvcResult mvcResult = mockMvc.perform(post("/api/v1/library/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + createJwtToken("admin"))
                        .content(objectMapper.writeValueAsString(book)))
                .andReturn();
        //then
        MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        ErrorResponse errorResponse = objectMapper.readValue(response.getContentAsString(), ErrorResponse.class);
        assertThat(errorResponse.getCode()).isEqualTo(4002);
        assertThat(errorResponse.getMessage()).isEqualTo("Author is mandatory");
    }

    @Test
    void shouldThrow403WhenTokenIsNotValid() throws Exception {
        //given
        Book book = new Book("1234578", "James Bond", "Spielberg", 1990);
        String request = objectMapper.writeValueAsString(book);
        //when
        MvcResult mvcResult = mockMvc.perform(post("/api/v1/library/books")
                        .header("Authorization", "Bearer " + createJwtToken("test"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andReturn();
        //then
        MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(403);
    }
}
