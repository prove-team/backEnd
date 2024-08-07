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

    @Query("SELECT p FROM Prove p " +
            "WHERE (p.startTime >= :startDateTime AND p.endTime <= :endDateTime) " +
            "OR (p.startTime <= :startDateTime AND p.endTime >= :startDateTime) " +
            "OR (p.startTime <= :endDateTime AND p.endTime >= :endDateTime)")
    List<Prove> findAllByDateWithImagesAndUser(@Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime);

    @Query("SELECT p FROM Prove p WHERE p.endTime BETWEEN :startDate AND :endDate AND p.user.username = :username")
    List<Prove> findAllByEndTimeBetweenAndUserId(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("username") String username);


    @Query("SELECT p FROM Prove p WHERE p.tags IN (:userTags)")
    List<Prove> findByTagsIn(List<Tags> userTags);

    //Tags로 page가져옴
    @Query("select p FROM Prove p where p.tags IN (:userTags)")
    Page<Prove> findAByTagAndPage(@Param("userTags") List<Tags> userTags, Pageable pageable);


    @Query("SELECT p FROM Prove p WHERE p.user.username = :username")
    Page<Prove> findByUserName(String username, Pageable pageable);
}
