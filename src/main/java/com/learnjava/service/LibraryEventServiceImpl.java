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
        libraryEventProducer.sendLibraryEvent(event);
        return event;
    }

    @Override
    public LibraryEvent updateLibraryEvent(LibraryEvent event) {
        if (event.getEventType() != LibraryEventType.UPDATE) {
            throw new InvalidLibraryEventException("eventType must be UPDATE for update request");
        }
        if (event.getLibraryEventId() == null) {
            throw new InvalidLibraryEventException("libraryEventId is required for UPDATE request");
        }
        if (event.getBook() == null) {
            throw new InvalidLibraryEventException("book is required for UPDATE request");
        }
        libraryEventProducer.sendLibraryEvent(event);
        return event;
    }
}