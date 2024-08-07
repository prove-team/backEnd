package com.prove.domain.Prove;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.prove.domain.Like.Like;
import com.prove.domain.Prove.Dto.ProveDto;
import com.prove.domain.Tags;
import com.prove.domain.User.UserEntity;
import com.prove.domain.comment.Comment;
import com.prove.domain.image.Image;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//TODO 메인페이지 쿠키안쓰고 string으로 하는거 구현-완료 실험 안함 -- 완료
//TODO 다대일 단방향으로 바꾸기 -- 아직 못함
//TODO comment like 했을때 이미 한애들 바꾸기  - 이미 좋아요 했는데 누르면, "User has already liked this comment" RuntimeException터짐
//TODO PAGING처리

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Prove {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Column
    private Long id;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private String openOrNot;

    @Column
    private Long importance;

    @Column
    private String shortWord;

    @Column
    private String success;

    @Column
    @Enumerated(EnumType.STRING)
    private Tags tags;

    @OneToMany(mappedBy = "prove", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "prove", cascade = CascadeType.ALL)
    private List<Image> imgList = new ArrayList<>();

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;


    @OneToMany(mappedBy = "prove",cascade = CascadeType.ALL)
    private List<Comment> commentList = new ArrayList<>();

    @Column
    private String color;

    @Builder
    public Prove(String openOrNot,String shortWord, Long importance, String tags, LocalDateTime startTime, LocalDateTime endTime,UserEntity user,String color) {
        this.user = user;
        this.openOrNot = openOrNot;
        this.importance = importance;
        this.tags = Tags.valueOf(tags);
        this.startTime = startTime;
        this.endTime = endTime;
        this.success="NotYet";
        this.imgList = new ArrayList<>();
        this.commentList = new ArrayList<>();
        this.shortWord = shortWord;
        this.color=color;
    }

    void completeProve(List<Image> imgList, ProveDto proveDto) {
        this.imgList = imgList;
        for (Image image : imgList) {
            image.setProve(this);
        }
        this.shortWord = proveDto.getShortWord();
        this.success = "Success";
    }

    public Long getLikeCount() {
        return (long) likes.size();
    }

    public void editProve(ProveDto proveDto, List<Image> imageList) {
        if (proveDto.getStartTime() != null) {
            this.startTime=proveDto.getStartTime();
        }

        if (proveDto.getEndTime() != null) {
            this.endTime=proveDto.getEndTime();
        }

        if (proveDto.getOpenOrNot() != null) {
            this.openOrNot=proveDto.getOpenOrNot();
        }

        if (proveDto.getShortWord() != null) {
            this.shortWord=proveDto.getShortWord();
        }

        if (proveDto.getSuccess() != null) {
            this.success=proveDto.getSuccess();
        }

        if (proveDto.getTags() != null) {
            this.tags=Tags.valueOf(proveDto.getTags());
        }

        if (proveDto.getImportance() != null) {
            this.importance=proveDto.getImportance();
        }

        if (imageList != null && !imageList.isEmpty()) {
            this.imgList = imageList;
            for (Image image : imgList) {
                image.setProve(this);
            }
        }

        if(proveDto.getColor() !=null){
            this.color= proveDto.getColor();
        }
    }

    public void addComment(Comment comm) {
        commentList.add(comm);
        comm.setProve(this);
    }

}
