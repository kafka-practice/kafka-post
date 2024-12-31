package org.example.kafkapost.dto.jpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostsDto {
    private Long postId;
    private Long userId;
    private String content;
    private boolean useYn;
}
