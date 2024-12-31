package org.example.kafkapost.mapper;

import org.example.kafkapost.common.mapper.GenericMapper;
import org.example.kafkapost.dto.jpa.UsersDto;
import org.example.kafkapost.adapter.out.persistence.entity.UsersEntity;
import org.example.kafkapost.grpc.UserProto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsersMapper extends GenericMapper<UsersDto, UsersEntity> {
    UsersDto protoToDto(UserProto.UsersRetrieveResponse response);
}
