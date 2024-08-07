package com.prove.domain.comment;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private String comment;
    private Long prove_id;
    private int comment_id;
    private Long like_count;
}
