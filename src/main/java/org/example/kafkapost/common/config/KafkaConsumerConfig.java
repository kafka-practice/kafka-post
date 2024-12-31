package org.example.kafkapost.common.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {
    @Value("${spring.kafka.consumer.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.properties.security.protocol}")
    private String securityProtocol;

    @Value("${spring.kafka.properties.sasl.mechanism}")
    private String saslMechanism;

    @Value("${spring.kafka.properties.sasl.jaas.config}")
    private String saslJaasConfig;

    @Value("${spring.kafka.properties.ssl.truststore.location}")
    private String truststoreLocation;

    @Value("${spring.kafka.properties.ssl.truststore.password}")
    private String truststorePassword;

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        props.put("security.protocol", securityProtocol);
        props.put("sasl.mechanism", saslMechanism);
        props.put("sasl.jaas.config", saslJaasConfig);
        props.put("ssl.truststore.location", truststoreLocation);
        props.put("ssl.truststore.password", truststorePassword);

        return new DefaultKafkaConsumerFactory<>(props);
    }

    /*
     * Kafka 리스너 컨테이너 팩토리 설정
     * Kafka 리스너가 메시지 수신 시 사용하는 설정 제공
     * 컨슈머 팩토리를 통해 생성된 컨슈머를 사용하여 메시지 수신
     *
     * 수동 ACK 모드 및 오류 핸들러를 포함
     * MANUAL_IMMEDIATE ACK 모드는 메시지가 처리된 후 수동으로 즉시 ACK 전송.
     *   -> 리스터 메서드에서 Acknowledgment.acknowledge() 메서드를 호출하여 수동으로 ACK 전송
     * 이 모드는 메시지가 제대로 처리되었을 때만 Kafka 브로커에 ACK 전송
     *   -> 메시지 신뢰성 높임
     *   -> acknowledgment.acknowledge(); 요거로 수동 ACK 전송
     * */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.setCommonErrorHandler(kafkaErrorHandler());
        return factory;
    }

    /*
     * Kafka 오류 핸들러 설정
     * 예외 발생 시 재시도 로직 or 사용자 정의 예외 처리 로직 추가 가능
     *
     * FixedBackOff를 사용하여 5초 간격으로 최대 3회 재시도 수행 설정
     * 재시도 중 예외 발생 시 로그에 기록
     * */
    @Bean
    public CommonErrorHandler kafkaErrorHandler() {
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(new FixedBackOff(5000L, 3));
        errorHandler.setRetryListeners((record, ex, deliveryAttempt) -> {
            System.err.println("Failed to process record: " + record + " after " + deliveryAttempt + " attempts");
            System.err.println("Exception: " + ex.getMessage());
        });
        return errorHandler;
    }
}
