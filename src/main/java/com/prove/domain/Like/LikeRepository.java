package com.prove.domain.Like;

import com.prove.domain.Prove.Prove;
import com.prove.domain.User.UserEntity;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LikeRepository extends JpaRepository <Like,Long> {
    boolean existsByProveAndUser(Prove prove, UserEntity user);
    // prove_id를 사용하여 특정 prove에 대한 like 수를 가져오는 방법
    @Query("SELECT COUNT(l) FROM Like l WHERE l.prove.id = :proveId")
    Long countLikesByProveId(@Param("proveId") Long proveId);

    @Modifying
    @Query("DELETE FROM Like l WHERE l.prove = :prove AND l.user = :userEntity")
    void deleteByProveWithUser(@Param("prove") Prove prove, @Param("userEntity") UserEntity userEntity);

}
