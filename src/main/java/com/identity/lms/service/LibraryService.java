package com.identity.lms.service;

import com.identity.lms.domain.Book;
import com.identity.lms.exception.BookBorrowException;
import com.identity.lms.exception.BookNotFoundException;
import com.identity.lms.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service to handle library functionality.
 */
@Service
public class LibraryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LibraryService.class);
    private final BookRepository bookRepository;

    /**
     * LibraryService creator.
     *
     * @param bookRepository {@link  BookRepository}
     */
    public LibraryService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * Adds a new book to the library
     *
     * @param book {@link Book}
     */
    public Book addBook(Book book) {
        LOGGER.debug("Adding book {}", book);
        return bookRepository.save(book);
    }

    /**
     * Removes a book from the library by ISBN
     *
     * @param isbn the book ISBN
     */
    @CacheEvict("booksById")
    public synchronized void removeBook(String isbn) {
        LOGGER.debug("Deleting book by isbn {}", isbn);
        if (bookRepository.deleteById(isbn) == null) {
            LOGGER.debug("Book with ISBN  {} is not found. throwing exception.", isbn);
            throw new BookNotFoundException(isbn);
        }
    }


    /**
     * Returns a book by its ISBN. <br/>
     * throws {@link BookNotFoundException} if book is not found with isbn.
     *
     * @param isbn the book ISBN
     * @return the {@link Book}
     */
    @CachePut("booksById")
    public Book findBookByISBN(String isbn) {
        LOGGER.debug("Finding book by isbn {}", isbn);
        return Optional.ofNullable(bookRepository.findById(isbn))
                .orElseThrow(() -> new BookNotFoundException(isbn));
    }

    /**
     * Returns a list of books by a given author
     *
     * @param author the author
     * @return list of {@link Book}
     */
    @CachePut("booksByAuthor")
    public List<Book> findBooksByAuthor(String author) {
        LOGGER.debug("Finding book by author {}", author);
        return bookRepository.findAll()
                .stream()
                .filter(book -> book.getAuthor().equalsIgnoreCase(author))
                .toList();
    }

    /**
     * Decreases the available copies of a book by 1. <br/>
     * throws {@link BookBorrowException} if number of available copies is zero.
     *
     * @param isbn the book isbn
     */
    @CacheEvict(value = {"booksByAuthor", "booksById"}, allEntries = true)
    public synchronized Book borrowBook(String isbn) {
        LOGGER.debug("Borrowing book by isbn {}", isbn);
        return Optional.of(findBookByISBN(isbn))
                .filter(book -> book.getAvailableCopies() > 0)
                .map(Book::decrementAvailableCopies)
                .orElseThrow(() -> new BookBorrowException(
                        "There are not enough copies to borrow with ISBN " + isbn));
    }

    /**
     * Increases the available copies of a book by 1
     *
     * @param isbn the book ISBN
     */
    @CacheEvict(value = {"booksByAuthor", "booksById"}, allEntries = true)
    public synchronized Book returnBook(String isbn) {
        LOGGER.debug("Returning book by isbn {}", isbn);
        Book book = findBookByISBN(isbn);
        if (book.getAvailableCopies() < book.getActualCopies()) {
            LOGGER.debug("Updated number of available copies for ISBN {} after returning.", isbn);
            return book.incrementAvailableCopies();
        }
        return book;
    }
}
