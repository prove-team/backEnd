package com.prove.domain.comment_like;

import com.prove.domain.User.UserEntity;
import com.prove.domain.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface CommentLikeRepository extends JpaRepository<CommentLike,Long> {
    boolean existsByCommentAndUser(Comment comment, UserEntity userEntity);
}
