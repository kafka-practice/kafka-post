package org.example.kafkapost.repository;

import org.example.kafkapost.adapter.out.persistence.entity.PostsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostsRepository extends JpaRepository<PostsEntity, Long> {
}
