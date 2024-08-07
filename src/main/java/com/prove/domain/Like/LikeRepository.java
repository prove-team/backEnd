package com.prove.domain.Like;

import com.prove.domain.Prove.Prove;
import com.prove.domain.User.UserEntity;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository <Like,Long> {
    boolean existsByProveAndUser(Prove prove, UserEntity user);
}
