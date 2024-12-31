package org.example.kafkapost.common.config.grpc;

import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.example.kafkapost.common.config.grpc.interceptor.ExceptionHandlingInterceptor;
import org.example.kafkapost.common.config.grpc.interceptor.GrpcMetadataInterceptor;
import org.springframework.context.annotation.Configuration;

/*
* gRPC 서버 설정 - 인터셉터를 설정하는 클래스
* gRPC 인터셉터는 SpringBoot에서 @GrpcGlobalServerInterceptor 어노테이션을 사용하여 전역적으로 등록 가능
* 한 번 설정해 두면, 해당 인터셉터는 모든 gRPC 요청에 대해 자동으로 적용됨
* GrpcConfig 클래스에서 인터셉터를 전역적으로 등록했기에, gRPC 서버 클래스 내의 모든 gRPC 메서드는 자동으로 이 Interceptor 영향을 받음
* 예를 들어. ExceptionHandlingInterceptor 가 전역 인터셉터로 설정되어 있다면, 모든 gRPC 호출에서 발생하는 예외는 해당 Interceptor를 통해 처리됨.
* */

@Configuration
public class GrpcConfig {

    @GrpcGlobalServerInterceptor
    GrpcMetadataInterceptor grpcMetadataInterceptor() {
        return new GrpcMetadataInterceptor();
    }

    /**
     * gRPC 예외 처리 인터셉터 설정
     */
    @GrpcGlobalServerInterceptor
    ExceptionHandlingInterceptor exceptionHandlingInterceptor() {
        return new ExceptionHandlingInterceptor();
    }
}
