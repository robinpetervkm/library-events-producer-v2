package com.learnjava.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BookValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validBook_shouldPassValidation() {
        Book book = new Book(1, "Clean Code", "Robert Martin");
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertTrue(violations.isEmpty());
    }

    @Test
    void nullBookId_shouldFailValidation() {
        Book book = new Book(null, "Clean Code", "Robert Martin");
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertEquals(1, violations.size());
        assertEquals("bookId is required", violations.iterator().next().getMessage());
    }

    @Test
    void blankBookName_shouldFailValidation() {
        Book book = new Book(1, "", "Robert Martin");
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertEquals(1, violations.size());
        assertEquals("bookName is required", violations.iterator().next().getMessage());
    }

    @Test
    void nullBookName_shouldFailValidation() {
        Book book = new Book(1, null, "Robert Martin");
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertEquals(1, violations.size());
        assertEquals("bookName is required", violations.iterator().next().getMessage());
    }

    @Test
    void blankBookAuthor_shouldFailValidation() {
        Book book = new Book(1, "Clean Code", "  ");
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertEquals(1, violations.size());
        assertEquals("bookAuthor is required", violations.iterator().next().getMessage());
    }

    @Test
    void nullBookAuthor_shouldFailValidation() {
        Book book = new Book(1, "Clean Code", null);
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertEquals(1, violations.size());
        assertEquals("bookAuthor is required", violations.iterator().next().getMessage());
    }

    @Test
    void allFieldsNull_shouldFailValidation() {
        Book book = new Book(null, null, null);
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertEquals(3, violations.size());
    }
}