package com.prove.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class PagedDTO<T> {
    private T content;
    @JsonProperty
    private PageMetaData pageMetaData;

}