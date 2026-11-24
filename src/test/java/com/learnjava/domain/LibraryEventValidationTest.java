package com.learnjava.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LibraryEventValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validLibraryEventAdd_shouldPassValidation() {
        Book book = new Book(1, "Clean Code", "Robert Martin");
        LibraryEvent event = new LibraryEvent(null, LibraryEventType.ADD, book);
        Set<ConstraintViolation<LibraryEvent>> violations = validator.validate(event);
        assertTrue(violations.isEmpty());
    }

    @Test
    void validLibraryEventUpdate_shouldPassValidation() {
        Book book = new Book(1, "Clean Code", "Robert Martin");
        LibraryEvent event = new LibraryEvent(1, LibraryEventType.UPDATE, book);
        Set<ConstraintViolation<LibraryEvent>> violations = validator.validate(event);
        assertTrue(violations.isEmpty());
    }

    @Test
    void nullEventType_shouldFailValidation() {
        Book book = new Book(1, "Clean Code", "Robert Martin");
        LibraryEvent event = new LibraryEvent(1, null, book);
        Set<ConstraintViolation<LibraryEvent>> violations = validator.validate(event);
        assertEquals(1, violations.size());
        assertEquals("eventType is required", violations.iterator().next().getMessage());
    }

    @Test
    void nullBook_shouldFailValidation() {
        LibraryEvent event = new LibraryEvent(1, LibraryEventType.ADD, null);
        Set<ConstraintViolation<LibraryEvent>> violations = validator.validate(event);
        assertEquals(1, violations.size());
        assertEquals("book is required", violations.iterator().next().getMessage());
    }

    @Test
    void invalidBook_shouldFailValidation() {
        Book book = new Book(null, null, null);
        LibraryEvent event = new LibraryEvent(1, LibraryEventType.ADD, book);
        Set<ConstraintViolation<LibraryEvent>> violations = validator.validate(event);
        assertEquals(3, violations.size());
    }

    @Test
    void updateWithoutLibraryEventId_passesBeanValidation() {
        // Bean validation doesn't enforce libraryEventId for UPDATE
        // That's a business rule enforced in service/controller layer
        Book book = new Book(1, "Clean Code", "Robert Martin");
        LibraryEvent event = new LibraryEvent(null, LibraryEventType.UPDATE, book);
        Set<ConstraintViolation<LibraryEvent>> violations = validator.validate(event);
        assertTrue(violations.isEmpty());
    }
}