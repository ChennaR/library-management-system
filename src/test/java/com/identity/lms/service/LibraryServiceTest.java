package com.identity.lms.service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.identity.lms.domain.Book;
import com.identity.lms.exception.BookBorrowException;
import com.identity.lms.exception.BookNotFoundException;
import com.identity.lms.repository.BookRepository;

/**
 * Test class for {@link LibraryService}.
 */
class LibraryServiceTest implements WithAssertions {

    private LibraryService libraryService;

    @BeforeEach
    void setUp() {
        libraryService = new LibraryService(new BookRepository());
    }

    @Test
    void shouldAddBookToRepository() {
        //given
        Book book = new Book("123456", "James Bond", "Steven", 1990);
        //when
        Book newBook = libraryService.addBook(book);
        //then
        Book bookByISBN = libraryService.findBookByISBN(newBook.getIsbn());
        assertThat(bookByISBN).isEqualTo(book);
    }

    @Test
    void shouldIncreaseNumberOfAvailableCopiesWhenAddSameBookToRepository() {
        //given
        Book expected = new Book("123456", "James Bond", "Steven", 1990)
                .availableCopies(2).actualCopies(2);
        //when
        List.of(new Book("123456", "James Bond", "Steven", 1990),
                        new Book("123456", "James Bond", "Steven", 1990))
                .forEach(book -> libraryService.addBook(book));
        //then
        Book bookByISBN = libraryService.findBookByISBN("123456");
        assertThat(bookByISBN).isEqualTo(expected);
    }

    @Test
    void shouldRemoveBookFromRepository() {
        //given
        Book book = new Book("123456", "James Bond", "Steven", 1990);
        Book newBook = libraryService.addBook(book);
        //when
        libraryService.removeBook(newBook.getIsbn());
        //then
        assertThatThrownBy(() -> libraryService.findBookByISBN(newBook.getIsbn()))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessage("Book with ISBN 123456 is not found");
    }

    @Test
    void shouldThrowExceptionWhenBookIsNotFoundWhileRemovingBookFromRepository() {
        //given
        //when
        assertThatThrownBy(() -> libraryService.removeBook("123876"))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessage("Book with ISBN 123876 is not found");
        //then
    }

    @Test
    void shouldReturnBookByISBN() {
        //given
        Book book = new Book("1234578", "James Bond", "Spielberg", 1990);
        Book newBook = libraryService.addBook(book);
        //when
        Book bookByISBN = libraryService.findBookByISBN(newBook.getIsbn());
        //then
        assertThat(bookByISBN).isEqualTo(book);
    }

    @Test
    void shouldReturnAllBooksByAuthor() {
        //given
        List<Book> books = List.of(
                new Book("1234578", "James Bond", "Spielberg", 1990),
                new Book("1234562", "Effective Java", "Spielberg", 1990)
        );
        books.forEach(book -> libraryService.addBook(book));
        //when
        List<Book> booksByAuthor = libraryService.findBooksByAuthor("Spielberg");
        //then
        assertThat(booksByAuthor).containsAll(books);
    }

    @Test
    void shouldBorrowABook() {
        //given
        Book book = new Book("1234578", "James Bond", "Spielberg", 1990);
        Book expected = new Book("1234578", "James Bond", "Spielberg", 1990)
                .availableCopies(0);
        Book newBook = libraryService.addBook(book);
        //when
        Book borrowedBook = libraryService.borrowBook(newBook.getIsbn());
        //then
        assertThat(borrowedBook).isEqualTo(expected);
        assertThat(libraryService.findBookByISBN(newBook.getIsbn())).isEqualTo(expected);
    }

    @Test
    void shouldReturnABook() {
        //given
        Book book = new Book("1234578", "James Bond", "Spielberg", 1990);
        Book expected = new Book("1234578", "James Bond", "Spielberg", 1990)
                .availableCopies(1);
        Book newBook = libraryService.addBook(book);
        libraryService.borrowBook(book.getIsbn());
        //when
        Book returnedBook = libraryService.returnBook(newBook.getIsbn());
        //then
        assertThat(returnedBook.getAvailableCopies()).isEqualTo(1);
        assertThat(returnedBook).isEqualTo(expected);
        assertThat(libraryService.findBookByISBN(newBook.getIsbn())).isEqualTo(expected);
    }

    @Test
    void shouldThrowExceptionWhenBookIsNotAvailableWithISBN() {
        //given
        //when
        assertThatThrownBy(() -> libraryService.findBookByISBN("1234578"))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessage("Book with ISBN 1234578 is not found");
        //then
    }

    @Test
    void shouldThrowBorrowBookExceptionWhenNotEnoughCopies() {
        //given
        Book book = new Book("1234578", "James Bond", "Spielberg", 1990)
                .availableCopies(0);
        Book newBook = libraryService.addBook(book);
        //when
        assertThatThrownBy(() -> libraryService.borrowBook(newBook.getIsbn()))
                .isInstanceOf(BookBorrowException.class)
                .hasMessage("There are not enough copies to borrow with ISBN 1234578");
        //then
    }

    @Test
    void shouldBorrowBookWhenMultipleRequestsAttempted() throws InterruptedException {
        //given
        Book book = new Book("1234578", "James Bond", "Spielberg", 1990)
                .availableCopies(2);
        Book newBook = libraryService.addBook(book);
        //when
        Runnable borrowBookTask = () -> libraryService.borrowBook(newBook.getIsbn());
        executeTasks(List.of(borrowBookTask, borrowBookTask));
        //then
        assertThat(libraryService.findBookByISBN("1234578").getAvailableCopies()).isEqualTo(0);
    }

    @Test
    void shouldReturnBookWhenMultipleRequestsAttempted() throws InterruptedException {
        //given
        Book book = new Book("1234578", "James Bond", "Spielberg", 1990);
        Book newBook = libraryService.addBook(book);
        //when
        Runnable returnBookTask = () -> libraryService.returnBook(newBook.getIsbn());
        executeTasks(List.of(returnBookTask, returnBookTask));
        //then
        assertThat(libraryService.findBookByISBN("1234578").getAvailableCopies()).isEqualTo(1);
    }

    private static void executeTasks(List<Runnable> borrowTasks) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        borrowTasks.forEach(executorService::execute);
        executorService.shutdown();
        while (!executorService.awaitTermination(200, TimeUnit.MILLISECONDS)) {
            System.out.println("Waiting for threads to terminate");
        }
    }
}
