package com.learnjava.service;

import com.learnjava.domain.LibraryEvent;
import com.learnjava.domain.LibraryEventType;
import com.learnjava.exception.InvalidLibraryEventException;
import com.learnjava.producer.LibraryEventProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class LibraryEventServiceImpl implements LibraryEventService {

    private static final Logger log = LoggerFactory.getLogger(LibraryEventServiceImpl.class);

    private final LibraryEventProducer libraryEventProducer;

    public LibraryEventServiceImpl(LibraryEventProducer libraryEventProducer) {
        this.libraryEventProducer = libraryEventProducer;
    }

    @Override
    public LibraryEvent publishNewLibraryEvent(LibraryEvent event) {
        sendEvent(event);
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
        sendEvent(event);
        return event;
    }

    private void sendEvent(LibraryEvent event) {
        try {
            libraryEventProducer.sendLibraryEvent(event);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while sending library event: {}", e.getMessage());
            throw new IllegalStateException("Interrupted while sending library event", e);
        } catch (ExecutionException e) {
            log.error("Failed to send library event: {}", e.getMessage());
            throw new IllegalStateException("Failed to send library event", e);
        }
    }
}