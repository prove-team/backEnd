package com.prove.domain.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.prove.domain.PageMetaData;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class PagedCommentDTO<T> {
    private T content;
    @JsonProperty
    private PageMetaData pageMetaData;

}
