package org.example.kafkapost.adapter.out.persistence.adapter;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.kafkapost.adapter.out.persistence.entity.UsersEntity;
import org.example.kafkapost.common.annotation.PersistenceAdapter;
import org.example.kafkapost.dto.jpa.UsersDto;
import org.example.kafkapost.mapper.UsersMapper;
import org.example.kafkapost.port.out.UsersCrudPort;
import org.example.kafkapost.repository.UsersRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@PersistenceAdapter
@Service
@RequiredArgsConstructor
public class UsersPersistenceAdapter implements UsersCrudPort {
    private final UsersRepository usersRepository;
    private final UsersMapper usersMapper;

    @Override
    public UsersDto signUp(UsersDto usersDto) {
        UsersEntity usersEntity = usersMapper.toEntity(usersDto);
        return usersMapper.toDto(usersRepository.save(usersEntity));
    }

    @Override
    public boolean existsByEmail(UsersDto usersDto) {
        return usersRepository.existsByEmail(usersDto.getEmail());
    }
}
