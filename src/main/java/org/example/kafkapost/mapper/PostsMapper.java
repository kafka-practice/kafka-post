package org.example.kafkapost.mapper;

import org.example.kafkapost.common.mapper.GenericMapper;
import org.example.kafkapost.dto.jpa.PostsDto;
import org.example.kafkapost.adapter.out.persistence.entity.PostsEntity;

public interface PostsMapper extends GenericMapper<PostsDto, PostsEntity> {
}
