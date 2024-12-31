package org.example.kafkapost.port.out;

import org.example.kafkapost.dto.jpa.UsersDto;

public interface UsersCrudPort {
    UsersDto signUp(UsersDto usersDto);
    boolean existsByEmail(UsersDto usersDto);
}
