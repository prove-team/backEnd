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
    private int id;

    private String username;

    private String password;

    private String role;

    @Column
    private Integer level;

    @Column
    private Integer lank;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private List<UserEntity> friends;

    @JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Prove> proves = new ArrayList<>();

    //mypage에서 내가쓴 댓글 찾기하려고
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    //회원가입할때, 좋아하는 태그 설정 -> all page접속시 해당 tag로 가져오기
    @ElementCollection
    @CollectionTable(
            name = "user_tags",
            joinColumns = @JoinColumn(name = "user_id")
    )

    @Enumerated(EnumType.STRING)
    private List<Tags> tags = new ArrayList<>();

    private String mainImage;

    public void addComment(Comment comm) {
        comments.add(comm);
        comm.setUser(this);
    }

    public void complit() {
        this.level++;
        if(level==10){
            this.level=0;
            this.lank++;
        }
    }
}