package org.example.kafkapost.common.annotation.trace;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.TraceFlags;
import io.opentelemetry.api.trace.TraceState;
import lombok.extern.slf4j.Slf4j;
import io.opentelemetry.context.Context;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/*
* MSA의 분산 추적을 위해 Trace Context를 추출하는 유틸리티 클래스
* Jaeger 를 사용하여 분산 추적을 수행하고 있으며, Kafka 메시지에서 traceparent 헤더를 추출하여 SpanContext 생성
* */
@Slf4j
@Component
public class TraceUtil {

    public Context extractContextFromRecord(ConsumerRecord<String, String> record) {
        String traceParent = null;

        for (Header header : record.headers()) {
            if ("traceparent".equals(header.key())) {
                traceParent = new String(header.value(), StandardCharsets.UTF_8);
                log.info("Extracted traceparent: {}", traceParent);
                break;
            }
        }

        if (traceParent != null) {
            String[] parts = traceParent.split("-");
            if (parts.length < 4) {
                log.error("Invalid traceparent header : {}", traceParent);
                return Context.current();
            }

            String traceId = parts[1];
            String spanId = parts[2];
            String traceFlags = parts[3];

            // 추출한 traceId, spanId를 사용하여 SpanContext 생성
            SpanContext spanContext = SpanContext.createFromRemoteParent(
                    traceId,
                    spanId,
                    TraceFlags.fromHex(traceFlags, 0),
                    TraceState.getDefault()
            );

            log.info("Created SpanContext with traceId: {}, spanId: {}", traceId, spanId);
            return Context.current().with(Span.wrap(spanContext));
        } else {
            log.warn("No traceparent header found in the kafka message");
            return Context.current();
        }
    }
}
