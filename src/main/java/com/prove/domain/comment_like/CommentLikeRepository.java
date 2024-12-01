package com.prove.domain.comment_like;

import com.prove.domain.User.UserEntity;
import com.prove.domain.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

public interface CommentLikeRepository extends JpaRepository<CommentLike,Long> {
    boolean existsByCommentAndUser(Comment comment, UserEntity userEntity);

    // 특정 Comment ID에 대한 모든 CommentLike의 갯수를 반환하는 메서드
    @Query("SELECT COUNT(cl) FROM CommentLike cl WHERE cl.comment.id = :commentId")
    Long countLikesByCommentId(@Param("commentId") Long commentId);

    @Modifying
    @Query("DELETE FROM CommentLike cl WHERE cl.comment.id = :commentId")
    void deleteByCommentId(@Param("commentId") Long commentId);

    // comment와 user를 통해 CommentLike 삭제하는 메서드
    @Modifying
    @Query("DELETE FROM CommentLike cl WHERE cl.comment = :comment AND cl.user = :userEntity")
    void deleteByCommentWithUser(@Param("comment") Comment comment, @Param("userEntity") UserEntity userEntity);

}
