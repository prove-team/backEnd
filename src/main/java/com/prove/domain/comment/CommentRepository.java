package com.prove.domain.comment;

import com.prove.domain.Prove.Prove;
import com.prove.domain.User.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c JOIN FETCH c.user u JOIN FETCH c.prove p WHERE u.username = :username ORDER BY c.createdAt DESC")
    Page<Comment> findByUserNameAndPage(@Param("username") String username, Pageable pageable);

    @Modifying
    @Query("DELETE FROM Comment c WHERE c.prove.id = :proveId")
    void deleteByProveId(@Param("proveId") Long proveId);

    // 특정 Prove ID에 해당하는 댓글 가져오기 (fetch join 적용)
    @Query("SELECT c FROM Comment c JOIN FETCH c.user u JOIN FETCH c.prove p WHERE p.id = :proveId")
    List<Comment> findByProveId(@Param("proveId") Long proveId);

    @Query("SELECT c FROM Comment c WHERE c.prove = :prove")
    List<Comment> findByProve(Prove prove);
}

