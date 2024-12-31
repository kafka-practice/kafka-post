package org.example.kafkapost.event.outbox;

public interface OutboxEvent {
    Long getPayload();
    String getEventType();
}
