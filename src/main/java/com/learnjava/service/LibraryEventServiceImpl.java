package com.learnjava.service;

import com.learnjava.domain.LibraryEvent;
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
}