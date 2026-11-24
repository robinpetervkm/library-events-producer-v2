package com.learnjava.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class LibraryEvent {

    private Integer libraryEventId;

    @NotNull(message = "eventType is required")
    private LibraryEventType eventType;

    @NotNull(message = "book is required")
    @Valid
    private Book book;

    public LibraryEvent() {
    }

    public LibraryEvent(Integer libraryEventId, LibraryEventType eventType, Book book) {
        this.libraryEventId = libraryEventId;
        this.eventType = eventType;
        this.book = book;
    }

    public Integer getLibraryEventId() {
        return libraryEventId;
    }

    public void setLibraryEventId(Integer libraryEventId) {
        this.libraryEventId = libraryEventId;
    }

    public LibraryEventType getEventType() {
        return eventType;
    }

    public void setEventType(LibraryEventType eventType) {
        this.eventType = eventType;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    @Override
    public String toString() {
        return "LibraryEvent{" +
                "libraryEventId=" + libraryEventId +
                ", eventType=" + eventType +
                ", book=" + book +
                '}';
    }
}