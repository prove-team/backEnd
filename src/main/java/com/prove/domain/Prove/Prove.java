package com.prove.domain.Prove;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.prove.domain.Like.Like;
import com.prove.domain.Prove.Dto.ProveDto;
import com.prove.domain.Tags;
import com.prove.domain.User.UserEntity;
import com.prove.domain.comment.Comment;
import com.prove.domain.image.Image;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


//TODO comment like 했을때 이미 한애들 바꾸기  - 이미 좋아요 했는데 누르면, "User has already liked this comment" RuntimeException터짐
//TODO PAGING처리

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Prove {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Column
    private Long id;


    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Column(nullable = false)
    private LocalDateTime startTime;


    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private Boolean openOrNot;

    @Column
    private Long importance;

    @Column
    private String shortWord;

    @Column
    private String success;

    @Column
    @Enumerated(EnumType.STRING)
    private Tags tags;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    @Column
    private String color;

    @Builder
    public Prove(Boolean openOrNot,String shortWord, Long importance, String tags, LocalDateTime startTime, LocalDateTime endTime,UserEntity user,String color) {
        this.user = user;
        this.openOrNot = openOrNot;
        this.importance = importance;
        this.tags = Tags.valueOf(tags);
        this.startTime = startTime;
        this.endTime = endTime;
        this.success="NotYet";
        this.shortWord = shortWord;
        this.color=color;
    }

    void completeProve(ProveDto proveDto) {
        this.shortWord = proveDto.getShortWord();
        this.success = "Success";
    }

    public void editProve(ProveDto proveDto) {
        if (proveDto.getStartTime() != null) {
            this.startTime=proveDto.getStartTime();
        }

        if (proveDto.getEndTime() != null) {
            this.endTime=proveDto.getEndTime();
        }

        if (proveDto.getOpenOrNot() != null) {
            this.openOrNot=proveDto.getOpenOrNot();
        }

        if (proveDto.getShortWord() != null) {
            this.shortWord=proveDto.getShortWord();
        }

        if (proveDto.getSuccess() != null) {
            this.success=proveDto.getSuccess();
        }

        if (proveDto.getTags() != null) {
            this.tags=Tags.valueOf(proveDto.getTags());
        }

        if (proveDto.getImportance() != null) {
            this.importance=proveDto.getImportance();
        }


        if(proveDto.getColor() !=null){
            this.color= proveDto.getColor();
        }
    }

}
