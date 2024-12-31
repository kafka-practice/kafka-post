package org.example.kafkapost.common.config.grpc.interceptor;

import io.grpc.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExceptionHandlingInterceptor implements ServerInterceptor {

    /*
    * gRPC 메서드 호출을 가로채고, 예외 처리
    * */
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> serverCall,
            Metadata metadata,
            ServerCallHandler<ReqT, RespT> serverCallHandler
    ) {
        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(
                serverCallHandler.startCall(new ExceptionHandlingServerCall<>(serverCall), metadata)
        ) {
            /*
            * 클라이언트의 요청 처리를 시도
            * 예외 발생 시, handleException 메서드가 호출됨
            * */
            @Override
            public void onHalfClose() {
                try {
                    super.onHalfClose();
                } catch (Exception e) {
                    handleException(serverCall, e);
                }
            }
        };
    }

    private <RespT> void handleException(ServerCall<RespT,?> serverCall, Exception e) {
        Status status;
        if (e instanceof IllegalArgumentException) {
            // 잘못된 인수 예외 처리
            status = Status.INVALID_ARGUMENT.withDescription(e.getMessage());
        } else {
            // 알 수 없는 예외 처리
            status = Status.UNKNOWN.withDescription("Unknown error occurred").withCause(e);
        }

        // 예외 로그 기록
        log.error("Exception : ", e);
        // gRPC 호출 종료 및 상태 코드 반환
        serverCall.close(status, new Metadata());
    }

    private static class ExceptionHandlingServerCall<ReqT, RespT>
            extends ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT> {

        /*
        * ExceptionHandlingServerCall 생성자
        *
        * @Param delegate - 실제 gRPC 서버 호출 객체
        * */
        protected ExceptionHandlingServerCall(ServerCall<ReqT, RespT> delegate) {
            super(delegate);
        }
    }
}
