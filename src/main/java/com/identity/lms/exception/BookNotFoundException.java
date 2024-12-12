package com.identity.lms.exception;

/**
 * Exception to handle book not available.
 */
public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(String isbn) {
        super(String.format("Book with ISBN %s is not found", isbn));
    }
}
