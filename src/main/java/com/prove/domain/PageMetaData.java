package com.prove.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PageMetaData {
    @JsonProperty
    private long size;
    @JsonProperty
    private long totalElements;
    @JsonProperty
    private long totalPages;
    @JsonProperty
    private long number;
}