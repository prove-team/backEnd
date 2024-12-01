package com.prove.domain.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FriendShipRepository extends JpaRepository<FriendShip, Long> {
    boolean existsByUserAndFriend(UserEntity user, UserEntity friend);

    FriendShip findByUserAndFriend(UserEntity user, UserEntity friend);

    @Query("SELECT fs FROM FriendShip fs JOIN FETCH fs.friend f WHERE fs.user = :userEntity")
    List<FriendShip> findByUserWithFriends(@Param("userEntity") UserEntity userEntity);

    List<FriendShip> findByUser(UserEntity userEntity);
}

