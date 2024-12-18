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
    private Long comment_id;
    private Long like_count;
    private String user_main_Img;
    private String username;
}
