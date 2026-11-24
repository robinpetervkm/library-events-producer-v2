package com.learnjava.producer;

import com.learnjava.config.AppConstants;
import com.learnjava.domain.LibraryEvent;
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

    private final KafkaTemplate<Long, LibraryEvent> kafkaTemplate;

    public LibraryEventProducer(KafkaTemplate<Long, LibraryEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendLibraryEvent(LibraryEvent event) {
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
}