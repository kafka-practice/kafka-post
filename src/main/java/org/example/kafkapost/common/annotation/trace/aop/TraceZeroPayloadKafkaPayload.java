package org.example.kafkapost.common.annotation.trace.aop;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.kafkapost.common.annotation.trace.util.TraceUtil;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
@Aspect
public class TraceZeroPayloadKafkaPayload {
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("kafka-consumer");
    private final TraceUtil traceUtil;

    /*
    * kafka 리스너 호출 시, Span 생성 및 종료
    *
    * @Param joinPoint  - 프록시 대상 메서드
    * @Param record     - Kafka 메시지
    * @Throws Throwable - 예외 처리
    * */
    @Around(value = "@annotation(org.example.kafkapost.common.annotation.trace.TraceZeroPayloadKafka) && " +
            "args(record, acknowledgment, partition, offset)",
            // @TraceZeroPayloadKafka 가 붙은 메서드와
            // 정확히 Parameter 순서가 args 순서인 메서드에만 적용
            argNames = "joinPoint, record, acknowledgment, partition, offset"
    ) public Object traceKafkaListener(
            ProceedingJoinPoint joinPoint,
            ConsumerRecord<String, String> record,
            Acknowledgment acknowledgment,
            int partition,
            long offset
    ) throws Throwable {
        // 1. Context 추출
        Context extractedContext = traceUtil.extractContextFromRecord(record);

        // 2. 기존 Trace 기반 새로운 Span 생성
        Span span = tracer.spanBuilder("kafka-post [Kafka-consume] zeroPayload gRPC call")
                .setParent(extractedContext)
                .startSpan();

        // 3. Span 을 현재 컨텍스트에 설정
        try (Scope scope = span.makeCurrent()) {
            // 3 - 1. 실제 메서드 호출
            return joinPoint.proceed();
        } catch (Exception e) {
            // 3 - 2. 예외 발생 시 Span 에 기록.
            span.recordException(e);
            throw e;
        } finally {
            // 4. Span 종료
            log.info("Ending traceId: {}, spanId: {}", span.getSpanContext().getTraceId(), span.getSpanContext().getSpanId());
            span.end();
        }
    }

}
