package org.example.kafkapost.adapter.out.grpc;

import io.grpc.Channel;
import io.grpc.ClientInterceptors;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapSetter;
import lombok.val;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.kafkapost.grpc.UserProto;
import org.example.kafkapost.grpc.UserServiceGrpc;
import org.example.kafkapost.port.out.grpc.GrpcUsersClientPort;
import org.springframework.stereotype.Component;

/*
* gRPC 클라이언트
* GrpcClient 클래스는 gRPC 클라이언트를 구현한 것
* 다른 서버 or 같은 서버 내에서 gRPC 서버 메서드를 호출하는 데 사용됨.
* 이 클래스는 애플리케이션 내에서 gRPC 서버에 요청을 보내는 역할 수행
* */
@Component
public class GrpcUsersClientAdapter implements GrpcUsersClientPort {

    // gRPC 서버에 연결하기 위한 blockingStub
    @GrpcClient("user-grpc-server")
    private UserServiceGrpc.UserServiceBlockingStub blockingStub;

    // gRPC 요청에서 사용할 Metadata 를 헤더로 추가하는 TextMapSetter
    private static final TextMapSetter<Metadata> setter =
            (carrier, key, value) -> carrier.put(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER), value);

    @Override
    public UserProto.UsersRetrieveResponse getMemberById(Long userId, Context context) {
        GrpcRequestResult result = createGrpcRequest(userId, context);
        return result.stubWithHeaders().getUsersById(result.request);
    }

    /*
    * @Param userId                  - userId
    * @Param context                 - 현재 컨텍스트
    * @return GrpcRequestResult
    * @apiNote Grpc Request 생성
    * Metadata 를 헤더로 추가하여 요청 (traceParent 헤더 주입)
    * */
    private GrpcRequestResult createGrpcRequest(Long userId, Context context) {
        Metadata metadata = new Metadata();
        GlobalOpenTelemetry.getPropagators().getTextMapPropagator().inject(context, metadata, setter);

        // 2. gRPC 요청을 위한 MemberIdRequest 객체 생성
        UserProto.UsersIdRequest request = UserProto.UsersIdRequest.newBuilder()
                .setUserId(userId)
                .build();

        // 3. Metadata 를 헤더로 추가하여 요청 (traceParent 헤더 주입)
        Channel interceptedChannel = ClientInterceptors.intercept(
                blockingStub.getChannel(), MetadataUtils.newAttachHeadersInterceptor(metadata));
        UserServiceGrpc.UserServiceBlockingStub stubWithHeaders = UserServiceGrpc.newBlockingStub(interceptedChannel);

        // 4. gRPC 요청 결과 반환
        return new GrpcRequestResult(request, stubWithHeaders);
    }

    /*
    * @Param request
    * @Param stubWithHeaders
    * @apiNote gRPC 요청 결과 dto
    * */
    private record GrpcRequestResult(UserProto.UsersIdRequest request,
                                     UserServiceGrpc.UserServiceBlockingStub stubWithHeaders) {
    }
}
