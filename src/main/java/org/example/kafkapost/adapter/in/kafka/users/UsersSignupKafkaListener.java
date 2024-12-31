package org.example.kafkapost.adapter.in.kafka.users;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.opentelemetry.context.Context;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.kafkapost.common.annotation.trace.TraceZeroPayloadKafka;
import org.example.kafkapost.dto.jpa.UsersDto;
import org.example.kafkapost.event.UsersSignupEvent;
import org.example.kafkapost.event.outbox.OutboxEvent;
import org.example.kafkapost.grpc.UserProto;
import org.example.kafkapost.mapper.UsersMapper;
import org.example.kafkapost.port.out.UsersCrudPort;
import org.example.kafkapost.port.out.grpc.GrpcUsersClientPort;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.Target;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsersSignupKafkaListener {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final GrpcUsersClientPort grpcUsersClientPort;
    private final UsersMapper usersMapper;
    private final UsersCrudPort usersCrudPort;

    /**
     * 유저가 생성되면 이벤트를 수신하고
     * gRPC 클라이언트를 통해 member 서버에 회원 정보를 요청한다.
     *
     * @param record         - Kafka 메시지
     * @param acknowledgment - ack 처리
     * @param partition      - partition
     * @param offset         - offset
     */
    @Transactional
    @TraceZeroPayloadKafka
    @KafkaListener(
            topics = {"users-signup-outbox"},
            groupId = "post-group-user-signup",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listenMemberCreate(
            ConsumerRecord<String, String> record,
            Acknowledgment acknowledgment,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) throws JsonProcessingException {
        // 1. 이벤트 객체로 변환한다.
        String jsonValue = record.value();
        OutboxEvent event = objectMapper.readValue(jsonValue, UsersSignupEvent.class);

        UserProto.UsersRetrieveResponse result =
                grpcUsersClientPort.getMemberById(event.getPayload(), Context.current());

        log.info(String.valueOf(result));

        // 이 부분은 제가 임의로 생각해서 작성한 부분입니다.
        // 데이터가 존재하지 않을때만 저장 수행.
        UsersDto usersDto = usersMapper.protoToDto(result);

        log.info(String.valueOf(usersDto));

        boolean exist = usersCrudPort.existsByEmail(usersDto);
        if (!exist) {
            usersCrudPort.signUp(usersDto);
        }
        // -------------------------------------

        // 3. ack 처리
        acknowledgment.acknowledge();
    }
}
