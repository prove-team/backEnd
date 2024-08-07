package com.prove.domain.Prove.Dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProveDto {

    private String openOrNot;

    private Long importance;

    private String shortWord;

    private String success;

    private String tags;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String color;
}
