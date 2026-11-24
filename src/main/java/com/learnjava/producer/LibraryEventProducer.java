package com.learnjava.producer;

import com.learnjava.config.AppConstants;
import com.learnjava.domain.LibraryEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class LibraryEventProducer {

    private static final Logger log = LoggerFactory.getLogger(LibraryEventProducer.class);

    private final KafkaTemplate<Long, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public LibraryEventProducer(KafkaTemplate<Long, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendLibraryEvent(LibraryEvent event) {
        Long key = event.getLibraryEventId() != null ? event.getLibraryEventId().longValue() : event.getBook().getBookId().longValue();

        String value;
        try {
            value = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize library event to JSON: libraryEventId={}, eventType={}, bookId={}, error={}",
                    event.getLibraryEventId(), event.getEventType(), event.getBook().getBookId(), e.getMessage());
            throw new IllegalStateException("Failed to serialize library event", e);
        }

        ProducerRecord<Long, String> record = new ProducerRecord<>(AppConstants.LIBRARY_EVENTS_TOPIC, key, value);

        CompletableFuture<SendResult<Long, String>> future = kafkaTemplate.send(record);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to send library event: libraryEventId={}, eventType={}, bookId={}, error={}",
                        event.getLibraryEventId(), event.getEventType(), event.getBook().getBookId(), ex.getMessage());
            } else {
                log.info("Successfully sent library event: libraryEventId={}, eventType={}, bookId={}, partition={}, offset={}",
                        event.getLibraryEventId(), event.getEventType(), event.getBook().getBookId(),
                        result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
            }
        });
    }
}