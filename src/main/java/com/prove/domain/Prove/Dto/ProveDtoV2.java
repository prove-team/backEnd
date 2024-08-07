package com.prove.domain.Prove.Dto;

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

    private String openOrNot;

    private Long importance;

    private String shortWord;

    private String success;

    private Tags tags;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private List<Image> imgList = new ArrayList<>();

    private UserDto user;

    private Long like;

    private List<CommentDto> commentDtoList = new ArrayList<>();

    private String color;
}