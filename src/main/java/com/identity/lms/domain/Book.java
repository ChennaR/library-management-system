package com.identity.lms.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

/**
 * Class to hold book details
 */
@Data
@NoArgsConstructor
public class Book {
    @NotBlank(message = "ISBN is mandatory")
    private String isbn;
    @NotBlank(message = "Title is mandatory")
    private String title;
    @NotBlank(message = "Author is mandatory")
    private String author;
    @Range(min = 1990, max = 2024, message = "PublicationYear must be between 1990 and 2024")
    private int publicationYear;
    private int availableCopies = 1;
    @JsonIgnore
    private int actualCopies = 1;

    public Book(String isbn, String title, String author, int publicationYear) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
    }

    public Book availableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
        return this;
    }

    public Book upDateCopies() {
        availableCopies++;
        actualCopies++;
        return this;
    }

    public Book decrementAvailableCopies() {
        availableCopies--;
        return this;
    }

    public Book incrementAvailableCopies() {
        availableCopies++;
        return this;
    }

    public Book actualCopies(int actualCopies) {
        this.actualCopies = actualCopies;
        return this;
    }
}
