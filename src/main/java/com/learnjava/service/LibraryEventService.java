package com.learnjava.service;

import com.learnjava.domain.LibraryEvent;

public interface LibraryEventService {
    LibraryEvent publishNewLibraryEvent(LibraryEvent event);
}