package com.prove.domain.User;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.prove.domain.Prove.Prove;
import com.prove.domain.Tags;
import com.prove.domain.comment.Comment;
import com.prove.domain.image.ImageDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
public class UserEntity {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    private String role;

    @Column
    private Integer level;

    @Column
    private Integer lank;

    //회원가입할때, 좋아하는 태그 설정 -> all page접속시 해당 tag로 가져오기
    @ElementCollection
    @CollectionTable(
            name = "user_tags",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Enumerated(EnumType.STRING)
    private List<Tags> tags = new ArrayList<>();

    private String mainImage="";

    public void complit() {
        this.level++;
        if(level==10){
            this.level=0;
            this.lank++;
        }
    }
}