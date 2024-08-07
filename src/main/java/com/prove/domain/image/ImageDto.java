package com.prove.domain.image;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//이거 Data,빌더,noArgsConstructor, ALlArgsConstructor 왜쓴거지?
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageDto {
    private String imgName;
    private String imgUrl;
}
