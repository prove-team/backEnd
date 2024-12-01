package com.prove.domain.User;

import com.prove.domain.Prove.Prove;
import com.prove.domain.User.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity,Integer> {

    Boolean existsByUsername(String username);

    UserEntity findByUsername(String username);


    @Query("SELECT u FROM UserEntity u WHERE u.username LIKE CONCAT('%', :username, '%')")
    Page<UserEntity> findByUsernameWithPage(String username, Pageable pageable);

}
