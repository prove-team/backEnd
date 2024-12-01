package com.prove.domain.Prove;

import com.prove.domain.Prove.Dto.ProveDtoV2;
import com.prove.domain.Tags;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ProveRepository extends JpaRepository<Prove, Long> {

    @Query("SELECT p FROM Prove p JOIN FETCH p.user u " +
            "WHERE u.username = :username " +
            "AND ((p.startTime >= :startDateTime AND p.endTime <= :endDateTime) " +
            "OR (p.startTime <= :startDateTime AND p.endTime >= :startDateTime) " +
            "OR (p.startTime <= :endDateTime AND p.endTime >= :endDateTime))")
    List<Prove> findAllByDateWithImagesAndUser(
            @Param("username") String username,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime);


    @Query("SELECT p FROM Prove p WHERE p.endTime BETWEEN :startDate AND :endDate AND p.user.username = :username")
    List<Prove> findAllByEndTimeBetweenAndUserId(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("username") String username);


    @Query("SELECT p FROM Prove p JOIN FETCH p.user u WHERE p.tags IN :userTags")
    List<Prove> findByTagsIn(@Param("userTags") List<Tags> userTags);

    //Tags로 page가져옴
    @Query("SELECT p FROM Prove p JOIN FETCH p.user u " +
            "WHERE p.tags IN (:userTags) " +
            "AND p.openOrNot = true " +
            "AND p.success = 'Success' " + // success가 "Success"인 조건 추가
            "ORDER BY ABS(TIMESTAMPDIFF(SECOND, p.endTime, CURRENT_TIMESTAMP)) ASC")
    Page<Prove> findAByTagAndPage(@Param("userTags") List<Tags> userTags, Pageable pageable);


    @Query("SELECT p FROM Prove p JOIN FETCH p.user u " +
            "WHERE u.username = :username " +
            "ORDER BY ABS(TIMESTAMPDIFF(SECOND, p.endTime, CURRENT_TIMESTAMP)) ASC")
    Page<Prove> findByUserName(@Param("username") String username, Pageable pageable);



    // 특정 UserEntity ID에 해당하는 모든 Prove를 Page 형태로 가져오는 메서드
    @Query("SELECT p FROM Prove p JOIN FETCH p.user u " +
            "WHERE u.id = :userId " +
            "AND p.openOrNot = true " +
            "ORDER BY ABS(TIMESTAMPDIFF(SECOND, p.endTime, CURRENT_TIMESTAMP)) ASC")
    Page<Prove> findAllByUserId(@Param("userId") Long userId, Pageable pageable);


    @Query("SELECT p FROM Prove p WHERE p.user.id = :userId")
    List<Prove> findProvesByUserId(@Param("userId") Long userId);


    //친구id를 넘겨주면 endTime순으로 가져옴
    @Query("SELECT p FROM Prove p JOIN FETCH p.user u WHERE p.user.id IN :friendIds AND p.openOrNot = true ORDER BY p.endTime DESC")
    Page<Prove> findByUserIdIn(@Param("friendIds") List<Long> friendIds, Pageable pageable);


    @Query("SELECT p FROM Prove p JOIN FETCH p.user u WHERE p.openOrNot = true AND p.success = 'Success'" +
            "ORDER BY ABS(TIMESTAMPDIFF(SECOND, p.endTime, CURRENT_TIMESTAMP)) ASC")
    Page<Prove> findAllByEndTime(Pageable pageable);

    @Query("SELECT p FROM Prove p Join fetch p.user JOIN Like l ON p.id = l.prove.id WHERE p.tags = 'STUDY' GROUP BY p.id ORDER BY COUNT(l.id) DESC")
    List<Prove> findTop100ByStudyTagOrderByLikeCountDesc();

}
