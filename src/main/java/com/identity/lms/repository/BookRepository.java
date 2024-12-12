package com.identity.lms.repository;

import com.identity.lms.domain.Book;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repository to manage library books.
 * this is to simulate repository layer for library.
 */
@Repository
public class BookRepository {

    private final ConcurrentHashMap<String, Book> books =
            new ConcurrentHashMap<>();

    /**
     * Returns all books from local store.
     *
     * @return collection of books
     */
    public Collection<Book> findAll() {
        return books.values();
    }

    /**
     * Returns {@link Book} from local store using isbn
     *
     * @param isbn the isbn
     * @return the book
     */
    public Book findById(String isbn) {
        return books.get(isbn);
    }

    /**
     * Saves {@link Book} to local store.
     *
     * @param book the book
     */
    public synchronized Book save(Book book) {
        return books.compute(book.getIsbn(),
                (isbn, oldBook) -> Optional.ofNullable(oldBook)
                        .map(Book::upDateCopies)
                        .orElse(book));
    }

    /**
     * Removes book from local store.
     *
     * @param isbn the isbn
     * @return deleted book or else null
     */
    public Book deleteById(String isbn) {
        return books.remove(isbn);
    }
}
