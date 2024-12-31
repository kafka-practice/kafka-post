package org.example.kafkapost.dto.jpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsersDto {
    private Long userId;
    private String name;
    private String nickname;
    private String email;
    private String password;
}
