package org.example.kafkapost.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExternalEventType {
    USERS_SIGNUP_OUTBOX("users-signup-outbox", "회원 가입 이벤트");

    // event type == kafka topic
    private final String type;
    private final String description;
}
