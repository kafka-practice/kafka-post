package org.example.kafkapost.common.annotation.trace;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ZeroPayload로 gRPC에 요청을 보내는 Kafka Consumer의 메서드에 적용해서 Span을 적용하는 어노테이션
 * Kafka Consumer의 메서드에서 ZeroPayload gRPC 요청에 대해 추적 정보를 기록하는 데 사용됨
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TraceZeroPayloadKafka {
}
