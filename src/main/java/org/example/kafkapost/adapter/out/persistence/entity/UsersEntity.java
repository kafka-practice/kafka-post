package org.example.kafkapost.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.kafkapost.common.entity.BaseEntity;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
@Where(clause = "use_yn = true")
public class UsersEntity extends BaseEntity {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 데이터 동기화가 수행되는 테이블은 해당 어노테이션이 있어서는 안됨
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "password")
    private String password;
}
