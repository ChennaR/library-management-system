package com.identity.lms.controller;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.identity.lms.common.ErrorResponse;
import com.identity.lms.exception.BookBorrowException;
import com.identity.lms.exception.BookNotFoundException;

/**
 * Global exception handler for library management system.
 */
@RestControllerAdvice
public class LibraryControllerAdvice {
    private static final Logger LOGGER = LoggerFactory.getLogger(LibraryControllerAdvice.class);

    @ExceptionHandler(BookBorrowException.class)
    public ResponseEntity<ErrorResponse> bookBorrowException(BookBorrowException bookBorrowException) {
        LOGGER.debug("Error occurred while borrowing a book", bookBorrowException);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(4000, bookBorrowException.getMessage()));
    }

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ErrorResponse> bookNotFoundException(BookNotFoundException bookNotFoundException) {
        LOGGER.debug("Error occurred while retrieving a book", bookNotFoundException);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(4001, bookNotFoundException.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validationException(MethodArgumentNotValidException methodArgumentNotValidException) {
        LOGGER.debug("Error occurred while adding a book", methodArgumentNotValidException);
        String errors = methodArgumentNotValidException.getBindingResult()
                .getFieldErrors()
                .stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(","));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(4002, errors));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> badCredentialsException(BadCredentialsException badCredentialsException){
        LOGGER.debug("Error occurred while authenticating user ", badCredentialsException);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(4003, "User credentials are not valid"));
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponse> throwable(Throwable throwable) {
        LOGGER.debug("Error occurred while processing request.", throwable);
        return ResponseEntity.internalServerError()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponse(5001, throwable.getMessage()));
    }
}
