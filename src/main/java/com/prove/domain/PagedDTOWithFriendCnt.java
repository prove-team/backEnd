package com.prove.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class PagedDTOWithFriendCnt<T> {
    private T content;
    @JsonProperty
    private PageMetaData pageMetaData;

    private int friendCnt;

    private String userImg;

    private Boolean check = false;
}