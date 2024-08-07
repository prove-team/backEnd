package com.prove.domain.comment;

import com.prove.domain.Like.Like;
import com.prove.domain.Prove.Prove;
import com.prove.domain.User.UserEntity;
import com.prove.domain.comment_like.CommentLike;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private int id;

    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    Prove prove;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentLike> commentLikes = new ArrayList<>();

    //, nullable = false, updatable = false 댓글 DB에서 다 지우고 시작
    @Column(name = "created_at")
    private LocalDateTime createdAt; // 작성 날짜 필드 추가

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now(); // 댓글 생성 시 현재 시간 저장
    }
    @Builder
    Comment(String comment, UserEntity user, Prove prove){
        this.comment = comment;
        this.user = user;
        this.prove = prove;
    }
}
