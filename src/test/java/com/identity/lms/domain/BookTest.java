package com.identity.lms.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static jakarta.validation.Validation.buildDefaultValidatorFactory;

/**
 * Test class for {@link Book}
 */
class BookTest implements WithAssertions {

    private final Validator validator;

    public BookTest() {
        try (ValidatorFactory validatorFactory = buildDefaultValidatorFactory()) {
            validator = validatorFactory.getValidator();
        }
    }

    @Test
    void shouldNotThrowValidationErrorsWhenBookIsValid() {
        //given
        Book book = new Book("123456", "James Bond", "Steven", 1990);
        //when
        Set<ConstraintViolation<Book>> constraintViolations = validator.validate(book);
        //then
        assertThat(constraintViolations).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("bookValidationData")
    void shouldThrowValidationErrorsWhenBookIsNotValid(Book book, int errorSize, List<String> errorMessages) {
        //given
        //when
        List<String> errors = validator.validate(book)
                .stream()
                .map(ConstraintViolation::getMessage)
                .toList();
        //then
        assertThat(errors)
                .hasSize(errorSize)
                .containsAll(errorMessages);
    }

    public static Stream<Arguments> bookValidationData() {
        return Stream.of(
                Arguments.of(new Book(null, null, null, 0), 4,
                        List.of("ISBN is mandatory", "Title is mandatory", "Author is mandatory",
                                "PublicationYear must be between 1990 and 2024")),
                Arguments.of(new Book("", "", "", 1990), 3,
                        List.of("ISBN is mandatory", "Title is mandatory", "Author is mandatory")),
                Arguments.of(new Book(" ", " ", "", 1990), 3,
                        List.of("ISBN is mandatory", "Title is mandatory", "Author is mandatory"))
        );
    }
}
