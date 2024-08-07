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
    public ResponseEntity<?> makeComment(String comment, UserEntity userEntity, Prove prove) {
        Comment comm = Comment.builder()
                .comment(comment)
                .build();
        userEntity.addComment(comm);
        prove.addComment(comm);
        commentRepository.save(comm);
        return new ResponseEntity<>("댓글작성 완료", HttpStatus.OK);
    }

    public List<CommentDto> makeCommentDtos(List<Comment> comments) {
        List<CommentDto> commentDtoList = new ArrayList<>();
        for (Comment comment : comments) {
            commentDtoList.add(new CommentDto(comment.getComment(),comment.getProve().getId(), comment.getId(), (long) comment.getCommentLikes().size()));
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

        if (commentLikeRepository.existsByCommentAndUser(comment, userEntity)) {
            throw new RuntimeException("User has already liked this comment");
        }

        CommentLike commentLike = new CommentLike();
        commentLike.setComment(comment);
        commentLike.setUser(userEntity);
        commentLikeRepository.save(commentLike);

        return new ResponseEntity<>("댓글 좋아요 완료",HttpStatus.OK);
    }
    private static String getUsername() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(username);
        return username;
    }

    public PagedDTO<List<CommentDto>> pagedCommentDTO(Pageable pageable) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Page<Comment> comments = commentRepository.findByUserNameAndPage(username,pageable);
        PageMetaData pageMetaData = new PageMetaData(comments.getSize(),
                comments.getTotalElements(), comments.getTotalPages(), comments.getNumber());
        List<Comment> commentList = comments.getContent();
        List<CommentDto> commentDtoList = makeCommentDtos(commentList);
        return PagedDTO.<List<CommentDto>>builder()
                .content(commentDtoList)
                .pageMetaData(pageMetaData)
                .build();
    }
}
