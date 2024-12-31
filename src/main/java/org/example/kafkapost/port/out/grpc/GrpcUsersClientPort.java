package org.example.kafkapost.port.out.grpc;

import io.opentelemetry.context.Context;
import org.example.kafkapost.grpc.UserProto;

public interface GrpcUsersClientPort {
    UserProto.UsersRetrieveResponse getMemberById(Long userId, Context context);
}
