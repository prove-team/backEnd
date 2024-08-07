package com.prove.domain.comment;

import com.prove.domain.User.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    List<Comment> findByUser(UserEntity user);

    @Query("SELECT c FROM Comment c JOIN c.user u WHERE u.username = :username ORDER BY c.createdAt DESC")
    Page<Comment> findByUserNameAndPage(@Param("username") String username, Pageable pageable);
}
