package com.prove.domain.User.Dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String username;
    private String role;
    private int lank;
    private int level;
    private String userImg;
}
