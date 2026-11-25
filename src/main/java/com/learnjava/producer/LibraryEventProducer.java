package com.learnjava.producer;

import com.learnjava.config.AppConstants;
import com.learnjava.config.KafkaProducerConfig;
import com.learnjava.domain.LibraryEvent;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
public class LibraryEventProducer {

    private static final Logger log = LoggerFactory.getLogger(LibraryEventProducer.class);

    private final KafkaTemplate<Long, LibraryEvent> kafkaTemplate;
    private final KafkaProducerConfig kafkaProducerConfig;

    public LibraryEventProducer(KafkaTemplate<Long, LibraryEvent> kafkaTemplate, KafkaProducerConfig kafkaProducerConfig) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaProducerConfig = kafkaProducerConfig;
    }

    /**
     * Sends a library event using the configured send mode (sync or async).
     * The mode is determined by the 'library.events.producer.send-mode' property.
     *
     * @param event the library event to send
     * @throws InterruptedException if the thread is interrupted while waiting (sync mode only)
     * @throws ExecutionException if the send operation fails (sync mode only)
     */
    public void sendLibraryEvent(LibraryEvent event) throws InterruptedException, ExecutionException {
        if (kafkaProducerConfig.isSync()) {
            sendLibraryEventSynchronous(event);
        } else {
            sendLibraryEventAsync(event);
        }
    }

    /**
     * Sends a library event asynchronously using KafkaTemplate (non-blocking).
     * This is the default behavior when send-mode is "async".
     *
     * @param event the library event to send
     */
    public void sendLibraryEventAsync(LibraryEvent event) {
        Long key = event.getLibraryEventId() != null ? event.getLibraryEventId().longValue() : event.getBook().getBookId().longValue();

        ProducerRecord<Long, LibraryEvent> record = new ProducerRecord<>(AppConstants.LIBRARY_EVENTS_TOPIC, key, event);

        CompletableFuture<SendResult<Long, LibraryEvent>> future = kafkaTemplate.send(record);

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

    /**
     * Sends a library event synchronously using KafkaTemplate (blocking).
     * This is used when send-mode is "sync".
     *
     * @param event the library event to send
     * @throws InterruptedException if the thread is interrupted while waiting
     * @throws ExecutionException if the send operation fails
     */
    public void sendLibraryEventSynchronous(LibraryEvent event) throws InterruptedException, ExecutionException {
        Long key = event.getLibraryEventId() != null ? event.getLibraryEventId().longValue() : event.getBook().getBookId().longValue();

        ProducerRecord<Long, LibraryEvent> record = new ProducerRecord<>(AppConstants.LIBRARY_EVENTS_TOPIC, key, event);

        // send() returns a CompletableFuture; calling get() makes it synchronous
        SendResult<Long, LibraryEvent> result = kafkaTemplate.send(record).get();

        log.info("Successfully sent library event synchronously: libraryEventId={}, eventType={}, bookId={}, partition={}, offset={}",
                event.getLibraryEventId(), event.getEventType(), event.getBook().getBookId(),
                result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
    }

    // ========================================================================
    // StringSerializer alternative (commented out for reference)
    // ========================================================================
    /*
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
    */
}