package org.example.kafkapost.common.annotation.trace.config;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.semconv.ResourceAttributes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class TracingConfig {
    @Value("${opentelemetry.tracer.exporter.otlp.endpoint}")
    private String url;

    @Value("${otel.resource.attributes.service.name}")
    private String serviceName;

    @Bean
    public OpenTelemetry openTelemetry() {
        OtlpHttpSpanExporter spanExporter = OtlpHttpSpanExporter.builder()
                .setEndpoint(url)
                .build();

        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
                .setResource(Resource.create(io.opentelemetry.api.common.Attributes.of(
                        ResourceAttributes.SERVICE_NAME, serviceName)))
                .addSpanProcessor(BatchSpanProcessor.builder(spanExporter).build())
                .build();

        OpenTelemetrySdk openTelemetrySdk = OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider)
                .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
                .build();

        GlobalOpenTelemetry.set(openTelemetrySdk);

        return openTelemetrySdk;
    }
}
