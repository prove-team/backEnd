package com.prove.domain.Prove.Dto;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.prove.domain.Like.Like;
import com.prove.domain.Tags;
import com.prove.domain.User.Dto.UserDto;
import com.prove.domain.comment.CommentDto;
import com.prove.domain.image.Image;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProveDtoV2 {
    private Long proveId;

    private Boolean openOrNot;

    private Long importance;

    private String shortWord;

    private String success;

    private Tags tags;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime startTime;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime endTime;

    private List<Image> imgList = new ArrayList<>();

    private UserDto user;

    private Long like;

    private List<CommentDto> commentDtoList = new ArrayList<>();

    private String color;
}