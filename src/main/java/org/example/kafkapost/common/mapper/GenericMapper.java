package org.example.kafkapost.common.mapper;

public interface GenericMapper<D, E> {

    D toDto(E entity);

    E toEntity(D dto);

}
