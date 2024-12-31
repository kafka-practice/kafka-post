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
@Table(name = "posts")
@Where(clause = "use_yn = true")
public class PostsEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UsersEntity user;

    @Column(name = "content")
    private String content;
}
