package com.identity.lms.exception;

/**
 * Exception to handle borrowing book failure
 */
public class BookBorrowException extends RuntimeException {
    public BookBorrowException(String message) {
        super(message);
    }
}
