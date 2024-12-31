package org.example.kafkapost.common.config.grpc.interceptor;

import io.grpc.*;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.TraceFlags;
import io.opentelemetry.api.trace.TraceState;
import lombok.extern.slf4j.Slf4j;

/*
* gRPC 서버 메서드 호출 시, 요청 헤더에서 TraceParent 헤더를 추출하며, SpanContext 생성
* gRPC의 인터셉터 동작이 필요한 경우, 지금처럼 Server Interceptor 구현
* */
@Slf4j
public class GrpcMetadataInterceptor implements ServerInterceptor {

    /*
    * gRPC 서버 메서드 호출 시, 요청 헤더에서 traceparent 헤더를 추출하여 SpanContext 생성
    *
    * @Param serverCall         - 서버 메서드 호출 객체
    * @Param metadata           - 요청 헤더
    * @Param serverCallHandler  - 다음 인터셉터
    * @Param ReqT               - 요청 객체 타입
    * @Param RespT              - 응답 객체 타입
    * */
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> serverCall,
            Metadata metadata,
            ServerCallHandler<ReqT, RespT> serverCallHandler
    ) {
        // 1. gRPC 헤더(Metadata)에 담긴 TraceParent 값 추출
        String traceparent = metadata.get(GrpcMetadata.TRACEPARENT_KEY);

        // 2. 추출한 traceparent 값을 사용하여 SpanContext를 생성하고, gRPC 전용 Context로 래핑
        io.opentelemetry.context.Context otelContext = createOtelContextFromTraceparent(traceparent);

        // 3. 현재 gRPC 컨텍스트에 2번에서 생성한 OpenTelemetry 컨텍스트를 추가하여 이후에 서비스 메서드에서 이를 사용 가능케 함.
        Context context = Context.current().withValue(GrpcMetadata.OTEL_CONTEXT_KEY, otelContext);
        log.info("Method: {}", serverCall.getMethodDescriptor().getFullMethodName());

        // 4. 다음 인터셉터를 호출합니다.
        return Contexts.interceptCall(context, serverCall, metadata, serverCallHandler);
    }

    /*
    * 헤더(Metadata)에서 추출한 tarceparent 값을 사용하여 SpanContext를 생성하고 Context로 래핑
    *
    * @Param traceparent - traceparent 헤더 값
    * @Return SpanContext - 추출한 SpanContext
    * */
    private io.opentelemetry.context.Context createOtelContextFromTraceparent(String traceparent) {
        // 1. traceparent 헤더가 없으면 root 컨텍스트 반환
        if (traceparent == null || traceparent.isEmpty()) {
            return io.opentelemetry.context.Context.root();
        }

        // 2. 추출한 traceparent에서 traceId, spanId, traceFlags 추출
        String[] parts = traceparent.split("-");
        if (parts.length != 4) {
            throw new IllegalArgumentException("Invalid traceparent format");
        }

        String traceId = parts[1];
        String spanId = parts[2];
        TraceFlags traceFlags = TraceFlags.fromHex(parts[3], 0);

        // 3. 추출한 값으로 SpanContext 생성
        SpanContext spanContext = SpanContext.createFromRemoteParent(
                traceId,
                spanId,
                traceFlags,
                TraceState.getDefault()
        );

        // 4. SpanContext 를 Context로 래핑하여 반환.
        return io.opentelemetry.context.Context.root().with(Span.wrap(spanContext));
    }
}
