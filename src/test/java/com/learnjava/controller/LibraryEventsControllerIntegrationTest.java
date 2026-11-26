package com.learnjava.controller;

import com.learnjava.domain.Book;
import com.learnjava.domain.LibraryEvent;
import com.learnjava.domain.LibraryEventType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
class LibraryEventsControllerIntegrationTest {

    @Container
    static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.8.0"));

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
        registry.add("library.events.topic", () -> "library-events");
    }

    @Autowired
    private WebApplicationContext webApplicationContext;

    private ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void postValidLibraryEvent_returns201_andPublishesToKafka() throws Exception {
        // Given: A valid LibraryEvent with eventType=ADD
        Book book = new Book(101, "Clean Code", "Robert Martin");
        LibraryEvent libraryEvent = new LibraryEvent(null, LibraryEventType.ADD, book);

        // Set up a consumer to verify the message was published to Kafka
        Map<String, Object> consumerProps = Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers(),
                ConsumerConfig.GROUP_ID_CONFIG, "test-group-" + System.currentTimeMillis(),
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class,
                JsonDeserializer.TRUSTED_PACKAGES, "com.learnjava.domain"
        );

        try (var consumer = new DefaultKafkaConsumerFactory<>(consumerProps).createConsumer()) {
            consumer.subscribe(Collections.singletonList("library-events"));

            // When: POST the library event
            mockMvc.perform(post("/v1/library-events")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(libraryEvent)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.eventType").value("ADD"))
                    .andExpect(jsonPath("$.book.bookName").value("Clean Code"))
                    .andExpect(jsonPath("$.book.bookAuthor").value("Robert Martin"));

            // Then: Verify the event was published to Kafka
            var received = consumer.poll(Duration.ofSeconds(10))
                    .records("library-events")
                    .iterator()
                    .next();
            assertThat(received).isNotNull();
            assertThat(received.key()).isInstanceOf(Long.class);
            assertThat(received.value()).isInstanceOf(LibraryEvent.class);
            LibraryEvent event = (LibraryEvent) received.value();
            assertThat(event.getEventType()).isEqualTo(LibraryEventType.ADD);
            assertThat(event.getBook().getBookName()).isEqualTo("Clean Code");
            assertThat(event.getBook().getBookAuthor()).isEqualTo("Robert Martin");
            // The key should be the bookId since libraryEventId is null
            assertThat(received.key()).isEqualTo(101L);
        }
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
}