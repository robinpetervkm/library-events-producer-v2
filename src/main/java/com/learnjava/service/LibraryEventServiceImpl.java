package com.learnjava.service;

import com.learnjava.domain.LibraryEvent;
import com.learnjava.domain.LibraryEventType;
import com.learnjava.exception.InvalidLibraryEventException;
import com.learnjava.producer.LibraryEventProducer;
import org.springframework.stereotype.Service;

@Service
public class LibraryEventServiceImpl implements LibraryEventService {

    private final LibraryEventProducer libraryEventProducer;

    public LibraryEventServiceImpl(LibraryEventProducer libraryEventProducer) {
        this.libraryEventProducer = libraryEventProducer;
    }

    @Override
    public LibraryEvent publishNewLibraryEvent(LibraryEvent event) {
        validateAddEvent(event);
        libraryEventProducer.sendLibraryEvent(event);
        return event;
    }

    private void validateAddEvent(LibraryEvent event) {
        if (event.getEventType() != LibraryEventType.ADD) {
            throw new InvalidLibraryEventException("eventType must be ADD for POST request");
        }
        if (event.getBook() == null) {
            throw new InvalidLibraryEventException("book is required");
        }
    }
}