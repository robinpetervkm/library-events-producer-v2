package com.learnjava.controller;

import com.learnjava.config.AppConstants;
import com.learnjava.domain.LibraryEvent;
import com.learnjava.domain.LibraryEventType;
import com.learnjava.exception.InvalidLibraryEventException;
import com.learnjava.service.LibraryEventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(AppConstants.API_BASE_PATH)
public class LibraryEventsController {

    private final LibraryEventService libraryEventService;

    public LibraryEventsController(LibraryEventService libraryEventService) {
        this.libraryEventService = libraryEventService;
    }

    @PostMapping
    public ResponseEntity<LibraryEvent> createLibraryEvent(@Valid @RequestBody LibraryEvent libraryEvent) {
        if (libraryEvent.getEventType() != LibraryEventType.ADD) {
            throw new IllegalArgumentException("eventType must be ADD for POST request");
        }
        LibraryEvent savedEvent = libraryEventService.publishNewLibraryEvent(libraryEvent);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEvent);
    }

    @PutMapping
    public ResponseEntity<LibraryEvent> updateLibraryEvent(@Valid @RequestBody LibraryEvent libraryEvent) {
        if (libraryEvent.getEventType() != LibraryEventType.UPDATE) {
            throw new IllegalArgumentException("eventType must be UPDATE for PUT request");
        }
        if (libraryEvent.getLibraryEventId() == null) {
            throw new InvalidLibraryEventException("libraryEventId is required for UPDATE request");
        }
        LibraryEvent updatedEvent = libraryEventService.updateLibraryEvent(libraryEvent);
        return ResponseEntity.ok(updatedEvent);
    }
}