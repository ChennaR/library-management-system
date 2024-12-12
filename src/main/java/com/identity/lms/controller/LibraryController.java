package com.identity.lms.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.identity.lms.domain.Book;
import com.identity.lms.service.LibraryService;

import jakarta.validation.Valid;

/**
 * Controller to provide library endpoints.
 */
@RestController
@RequestMapping("/api/v1/library")
public class LibraryController {

    private final LibraryService libraryService;

    /**
     * LibraryController creator.
     *
     * @param libraryService {@link LibraryService}
     */
    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/books")
    public Book addBook(@RequestBody @Valid Book book) {
        return libraryService.addBook(book);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/books/{isbn}")
    public void removeBook(@PathVariable("isbn") String isbn) {
        libraryService.removeBook(isbn);
    }

    @GetMapping("/books/isbn/{isbn}")
    public Book getBookByIsbn(@PathVariable("isbn") String isbn) {
        return libraryService.findBookByISBN(isbn);
    }

    @GetMapping("/books/author/{author}")
    public List<Book> getBookByAuthor(@PathVariable("author") String author) {
        return libraryService.findBooksByAuthor(author);
    }

    @PatchMapping("/books/borrow/{isbn}")
    public Book borrowBook(@PathVariable("isbn") String isbn) {
        return libraryService.borrowBook(isbn);
    }

    @PatchMapping("/books/return/{isbn}")
    public Book returnBook(@PathVariable("isbn") String isbn) {
        return libraryService.returnBook(isbn);
    }
}
