package com.prove.domain.comment;

import com.prove.domain.PageMetaData;
import com.prove.domain.PagedDTO;
import com.prove.domain.Prove.Dto.ProveDtoV2;
import com.prove.domain.Prove.Prove;
import com.prove.domain.Tags;
import com.prove.domain.User.UserEntity;
import com.prove.domain.User.UserRepository;
import com.prove.domain.comment_like.CommentLike;
import com.prove.domain.comment_like.CommentLikeRepository;
import com.prove.domain.image.Image;
import com.prove.domain.image.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final ImageRepository imageRepository;
    public ResponseEntity<?> makeComment(String comment, UserEntity userEntity, Prove prove) {
        Comment comm = Comment.builder()
                .comment(comment)
                .build();
        //comment에서 userEnitty와 prove를 연관관계매핑해줘야함
        comm.mappingProveAndUser(userEntity,prove);
        System.out.println(comm.getUser());
        commentRepository.save(comm);
        List<String> response = new ArrayList<>();
        response.add(userEntity.getUsername());
        response.add(userEntity.getMainImage());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public List<CommentDto> makeCommentDtos(List<Comment> comments) {
        List<CommentDto> commentDtoList = new ArrayList<>();
        for (Comment comment : comments) {
            Long counted = commentLikeRepository.countLikesByCommentId(comment.getId());
            commentDtoList.add(
                    new CommentDto(comment.getComment(),comment.getProve().getId(), comment.getId(), counted,comment.getUser().getMainImage(),comment.getUser().getUsername())
            );
        }
        return commentDtoList;
    }

    public ResponseEntity<?> editComment(Long commentId, String comment) {
        Optional<Comment> comment1 = commentRepository.findById(commentId);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if(comment1.get().user.getUsername().equals(username)){
            comment1.get().setComment(comment);
            return new ResponseEntity<>("Comment 수정 완료!", HttpStatus.OK);
        }
        return new ResponseEntity<>("권한이 없습니다.",HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<?> deleteComment(Long commentId) {
        Optional<Comment> comment1 = commentRepository.findById(commentId);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if(comment1.get().user.getUsername().equals(username)){
            commentRepository.deleteById(commentId);
            return new ResponseEntity<>("Comment 삭제 완료!", HttpStatus.OK);
        }
        return new ResponseEntity<>("권한이 없습니다.",HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<?> addCommentLike(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("Comment not found"));
        String username = getUsername();
        UserEntity userEntity = userRepository.findByUsername(username);

        //TODO 이게 아니라 좋아요 감소로,
        //TODO 바로 보여줄려면 이걸 refresh 해줘야하나? 검증필요
        if (commentLikeRepository.existsByCommentAndUser(comment, userEntity)) {
            //1번 commentLikeRepository에서 해당 유저를 삭제
            commentLikeRepository.deleteByCommentWithUser(comment,userEntity);
            return new ResponseEntity<>("이미 좋아요를 눌렀어요.", HttpStatus.OK);
            //throw new RuntimeException("User has already liked this comment");
        }else {
            CommentLike commentLike = new CommentLike();
            commentLike.setComment(comment);
            commentLike.setUser(userEntity);
            commentLikeRepository.save(commentLike);
            return new ResponseEntity<>("댓글 좋아요 완료", HttpStatus.OK);
        }
    }
    private static String getUsername() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(username);
        return username;
    }

    public PagedDTO<List<CommentDtoV2>> pagedCommentDTO(Pageable pageable) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByUsername(username);
        Page<Comment> comments = commentRepository.findByUserNameAndPage(username, pageable);
        PageMetaData pageMetaData = new PageMetaData(comments.getSize(),
                comments.getTotalElements(), comments.getTotalPages(), comments.getNumber());
        List<Comment> commentList = comments.getContent();
        List<CommentDtoV2> commentDtoList = makeCommentDtoV2s(commentList);
        return PagedDTO.<List<CommentDtoV2>>builder()
                .content(commentDtoList)
                .pageMetaData(pageMetaData)
                .userImg(userEntity.getMainImage())
                .build();
    }

    private List<CommentDtoV2> makeCommentDtoV2s(List<Comment> commentList) {
        List<CommentDtoV2> commentDtoList = new ArrayList<>();
        for (Comment comment : commentList) {
            Long counted = commentLikeRepository.countLikesByCommentId(comment.getId());
            Prove prove = comment.getProve();
            Image firstByProve = imageRepository.findFirstByProve(prove);
            if (firstByProve != null) {
                commentDtoList.add(
                        new CommentDtoV2(comment.getComment(), prove.getId(), comment.getId(), counted, comment.getUser().getMainImage(), comment.getUser().getUsername(), firstByProve.getImgUrl())
                );
            } else {
                commentDtoList.add(
                        new CommentDtoV2(comment.getComment(), prove.getId(), comment.getId(), counted, comment.getUser().getMainImage(), comment.getUser().getUsername(), "")
                );
            }
        }
        return commentDtoList;
    }
}
