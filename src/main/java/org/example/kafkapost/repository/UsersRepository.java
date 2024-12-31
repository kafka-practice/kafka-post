package org.example.kafkapost.repository;

import org.example.kafkapost.adapter.out.persistence.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<UsersEntity, Long> {
    boolean existsByEmail (String email);
}
