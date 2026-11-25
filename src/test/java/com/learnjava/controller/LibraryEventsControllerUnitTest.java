package com.learnjava.controller;

import com.learnjava.domain.Book;
import com.learnjava.domain.LibraryEvent;
import com.learnjava.domain.LibraryEventType;
import com.learnjava.exception.GlobalExceptionHandler;
import com.learnjava.service.LibraryEventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LibraryEventsControllerUnitTest {

    private LibraryEventService libraryEventService = mock(LibraryEventService.class);
    private LibraryEventsController controller = new LibraryEventsController(libraryEventService);
    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void postValidLibraryEvent_returns201() throws Exception {
        // Given: A valid LibraryEvent with eventType=ADD
        Book book = new Book(101, "Clean Code", "Robert Martin");
        LibraryEvent libraryEvent = new LibraryEvent(null, LibraryEventType.ADD, book);
        LibraryEvent savedEvent = new LibraryEvent(1, LibraryEventType.ADD, book);

        when(libraryEventService.publishNewLibraryEvent(any(LibraryEvent.class))).thenReturn(savedEvent);

        // When & Then
        mockMvc.perform(post("/v1/library-events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(libraryEvent)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.libraryEventId").value(1))
                .andExpect(jsonPath("$.eventType").value("ADD"));
    }

    @Test
    void postLibraryEvent_invalidEventType_returns400() throws Exception {
        // Given: eventType=UPDATE is invalid for POST
        Book book = new Book(101, "Clean Code", "Robert Martin");
        LibraryEvent libraryEvent = new LibraryEvent(null, LibraryEventType.UPDATE, book);

        // When & Then
        mockMvc.perform(post("/v1/library-events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(libraryEvent)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postLibraryEvent_missingEventType_returns400() throws Exception {
        // Given: eventType is null
        Book book = new Book(101, "Clean Code", "Robert Martin");
        LibraryEvent libraryEvent = new LibraryEvent(null, null, book);

        // When & Then
        mockMvc.perform(post("/v1/library-events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(libraryEvent)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postLibraryEvent_missingBook_returns400() throws Exception {
        // Given: book is null
        LibraryEvent libraryEvent = new LibraryEvent(null, LibraryEventType.ADD, null);

        // When & Then
        mockMvc.perform(post("/v1/library-events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(libraryEvent)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postLibraryEvent_missingBookId_returns400() throws Exception {
        // Given: bookId is null
        Book book = new Book(null, "Clean Code", "Robert Martin");
        LibraryEvent libraryEvent = new LibraryEvent(null, LibraryEventType.ADD, book);

        // When & Then
        mockMvc.perform(post("/v1/library-events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(libraryEvent)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postLibraryEvent_blankBookName_returns400() throws Exception {
        // Given: bookName is blank
        Book book = new Book(101, "   ", "Robert Martin");
        LibraryEvent libraryEvent = new LibraryEvent(null, LibraryEventType.ADD, book);

        // When & Then
        mockMvc.perform(post("/v1/library-events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(libraryEvent)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postLibraryEvent_blankBookAuthor_returns400() throws Exception {
        // Given: bookAuthor is blank
        Book book = new Book(101, "Clean Code", "   ");
        LibraryEvent libraryEvent = new LibraryEvent(null, LibraryEventType.ADD, book);

        // When & Then
        mockMvc.perform(post("/v1/library-events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(libraryEvent)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void putValidLibraryEvent_returns200() throws Exception {
        // Given: A valid LibraryEvent with eventType=UPDATE and libraryEventId
        Book book = new Book(101, "Clean Code", "Robert Martin");
        LibraryEvent libraryEvent = new LibraryEvent(1, LibraryEventType.UPDATE, book);
        LibraryEvent updatedEvent = new LibraryEvent(1, LibraryEventType.UPDATE, book);

        when(libraryEventService.updateLibraryEvent(any(LibraryEvent.class))).thenReturn(updatedEvent);

        // When & Then
        mockMvc.perform(put("/v1/library-events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(libraryEvent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.libraryEventId").value(1))
                .andExpect(jsonPath("$.eventType").value("UPDATE"));
    }

    @Test
    void putLibraryEvent_invalidEventType_returns400() throws Exception {
        // Given: eventType=ADD is invalid for PUT
        Book book = new Book(101, "Clean Code", "Robert Martin");
        LibraryEvent libraryEvent = new LibraryEvent(1, LibraryEventType.ADD, book);

        // When & Then
        mockMvc.perform(put("/v1/library-events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(libraryEvent)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void putLibraryEvent_missingLibraryEventId_returns400() throws Exception {
        // Given: libraryEventId is null
        Book book = new Book(101, "Clean Code", "Robert Martin");
        LibraryEvent libraryEvent = new LibraryEvent(null, LibraryEventType.UPDATE, book);

        // When & Then
        mockMvc.perform(put("/v1/library-events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(libraryEvent)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void putLibraryEvent_missingEventType_returns400() throws Exception {
        // Given: eventType is null
        Book book = new Book(101, "Clean Code", "Robert Martin");
        LibraryEvent libraryEvent = new LibraryEvent(1, null, book);

        // When & Then
        mockMvc.perform(put("/v1/library-events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(libraryEvent)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void putLibraryEvent_missingBook_returns400() throws Exception {
        // Given: book is null
        LibraryEvent libraryEvent = new LibraryEvent(1, LibraryEventType.UPDATE, null);

        // When & Then
        mockMvc.perform(put("/v1/library-events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(libraryEvent)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void putLibraryEvent_missingBookId_returns400() throws Exception {
        // Given: bookId is null
        Book book = new Book(null, "Clean Code", "Robert Martin");
        LibraryEvent libraryEvent = new LibraryEvent(1, LibraryEventType.UPDATE, book);

        // When & Then
        mockMvc.perform(put("/v1/library-events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(libraryEvent)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void putLibraryEvent_blankBookName_returns400() throws Exception {
        // Given: bookName is blank
        Book book = new Book(101, "   ", "Robert Martin");
        LibraryEvent libraryEvent = new LibraryEvent(1, LibraryEventType.UPDATE, book);

        // When & Then
        mockMvc.perform(put("/v1/library-events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(libraryEvent)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void putLibraryEvent_blankBookAuthor_returns400() throws Exception {
        // Given: bookAuthor is blank
        Book book = new Book(101, "Clean Code", "   ");
        LibraryEvent libraryEvent = new LibraryEvent(1, LibraryEventType.UPDATE, book);

        // When & Then
        mockMvc.perform(put("/v1/library-events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(libraryEvent)))
                .andExpect(status().isBadRequest());
    }
}